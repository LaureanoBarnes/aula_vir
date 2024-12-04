
package holaSpringData.web;

import holaSpringData.clases.Foro;
import holaSpringData.clases.Mensaje;
import holaSpringData.dto.ForoDTO;
import holaSpringData.dto.MensajeDTO;
import holaSpringData.servicio.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aulas/{id_aula}/foros")
public class ForoController {

    private final ForoServicio foroServicio;
    private final MensajeServicio mensajeServicio;
    private final ReaccionServicioImpl reaccionServicioImpl;
    private final AulaServicio aulaServicio;
    private final UsuarioAutenticacionServicio usuarioAutenticacionServicio;

    public ForoController(ForoServicio foroServicio, MensajeServicio mensajeServicio, ReaccionServicioImpl reaccionServicioImpl, AulaServicio aulaServicio, UsuarioAutenticacionServicio usuarioAutenticacionServicio) {
        this.foroServicio = foroServicio;
        this.mensajeServicio = mensajeServicio;
        this.reaccionServicioImpl = reaccionServicioImpl;
        this.aulaServicio = aulaServicio;
        this.usuarioAutenticacionServicio = usuarioAutenticacionServicio;
    }

    @GetMapping
    public ResponseEntity<List<ForoDTO>> listarForos(@PathVariable Long id_aula) {
        List<Foro> foros = foroServicio.listarForosPorAula(id_aula);

        // Convertir a DTO
        List<ForoDTO> foroDTOs = foros.stream().map(foro -> new ForoDTO(
                foro.getId(),
                foro.getTitulo(),
                foro.getDescripcion(),
                foro.getAutor().getNombre() + " " + foro.getAutor().getApellido(),
                foro.getFechaCreacion().toString()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(foroDTOs);
    }

    @GetMapping("/{foro_id}")
    public ResponseEntity<ForoDTO> verForo(@PathVariable Long id_aula, @PathVariable Long foro_id) {
        Foro foro = foroServicio.encontrarForo(foro_id);

        // Crear el DTO para evitar el bucle infinito
        ForoDTO foroDTO = new ForoDTO(
                foro.getId(),
                foro.getTitulo(),
                foro.getDescripcion(),
                foro.getAutor().getNombre() + " " + foro.getAutor().getApellido(),
                foro.getFechaCreacion().toString()
        );

        return ResponseEntity.ok(foroDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Foro> crearForo(@PathVariable Long id_aula,
                                          @RequestBody ForoDTO foroDTO,
                                          @AuthenticationPrincipal User usuario) {
        Foro foro = new Foro();
        foro.setTitulo(foroDTO.getTitulo());
        foro.setDescripcion(foroDTO.getDescripcion());
        foro.setAula(foroServicio.encontrarAula(id_aula));
        foro.setAutor(foroServicio.encontrarIndividuoPorUsername(usuario.getUsername()));
        Foro nuevoForo = foroServicio.crearForo(foro);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoForo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{foro_id}")
    public ResponseEntity<Foro> actualizarForo(@PathVariable Long foro_id, @RequestBody Foro foro) {
        foro.setId(foro_id);
        Foro foroActualizado = foroServicio.actualizarForo(foro);
        return ResponseEntity.ok(foroActualizado);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{foro_id}")
    public ResponseEntity<Void> eliminarForo(@PathVariable Long foro_id) {
        foroServicio.eliminarForo(foro_id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{foro_id}/mensajes")
    public ResponseEntity<List<MensajeDTO>> listarMensajes(@PathVariable Long foro_id, @AuthenticationPrincipal User usuario) {
        try {
            // Get the current user's ID
            Long currentUserId = foroServicio.encontrarIndividuoPorUsername(usuario.getUsername()).getId_individuo();

            List<Mensaje> mensajes = foroServicio.listarMensajesPorForo(foro_id);

            // First convert all messages to DTOs
            Map<Long, MensajeDTO> mensajesMap = new HashMap<>();
            List<MensajeDTO> mensajesRaiz = new ArrayList<>();

            // First pass: create all DTOs
            for (Mensaje mensaje : mensajes) {
                MensajeDTO dto = new MensajeDTO(mensaje);

                // Check if the current user has liked this message
                boolean hasLiked = mensaje.getReacciones().stream()
                        .anyMatch(r -> r.getIndividuo().getId_individuo().equals(currentUserId) && r.getTipo().equals("like"));

                dto.setHasLiked(hasLiked);
                mensajesMap.put(dto.getId(), dto);
            }

            // Second pass: organize hierarchical structure
            for (Mensaje mensaje : mensajes) {
                MensajeDTO dto = mensajesMap.get(mensaje.getId());
                if (mensaje.getMensajePadre() != null) {
                    // It's a reply, add it to its parent
                    MensajeDTO padre = mensajesMap.get(mensaje.getMensajePadre().getId());
                    if (padre != null) {
                        padre.getRespuestas().add(dto);
                    }
                } else {
                    // It's a root message
                    mensajesRaiz.add(dto);
                }
            }

            // Sort by creation date (most recent first)
            mensajesRaiz.sort((m1, m2) -> m2.getFechaCreacion().compareTo(m1.getFechaCreacion()));

            return ResponseEntity.ok(mensajesRaiz);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{foro_id}/mensajes")
    public ResponseEntity<Mensaje> agregarComentario(@PathVariable Long foro_id,
                                                     @RequestBody Mensaje mensaje,
                                                     @AuthenticationPrincipal User usuario) {
        mensaje.setForo(foroServicio.encontrarForo(foro_id));
        mensaje.setAutor(foroServicio.encontrarIndividuoPorUsername(usuario.getUsername()));
        mensaje.setFechaCreacion(LocalDateTime.now());
        Mensaje nuevoMensaje = mensajeServicio.crearMensaje(mensaje);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoMensaje);
    }

    @PostMapping("/{foro_id}/mensajes/{mensaje_id}/respuestas")
    public ResponseEntity<Mensaje> agregarRespuesta(@PathVariable Long foro_id,
                                                    @PathVariable Long mensaje_id,
                                                    @RequestBody Mensaje respuesta,
                                                    @AuthenticationPrincipal User usuario) {
        Mensaje mensajePadre = mensajeServicio.encontrarMensajePorId(mensaje_id);
        respuesta.setMensajePadre(mensajePadre);
        respuesta.setForo(foroServicio.encontrarForo(foro_id));
        respuesta.setAutor(foroServicio.encontrarIndividuoPorUsername(usuario.getUsername()));
        respuesta.setFechaCreacion(LocalDateTime.now());
        Mensaje nuevaRespuesta = mensajeServicio.crearMensaje(respuesta);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaRespuesta);
    }

    @PostMapping("/mensajes/{mensaje_id}/like")
    public ResponseEntity<Integer> alternarLike(@PathVariable Long mensaje_id,
                                                @AuthenticationPrincipal User usuario) {
        Long individuoId = foroServicio.encontrarIndividuoPorUsername(usuario.getUsername()).getId_individuo();
        int likesActualizados = reaccionServicioImpl.alternarReaccion(mensaje_id, individuoId, "like");
        return ResponseEntity.ok(likesActualizados);
    }
}
