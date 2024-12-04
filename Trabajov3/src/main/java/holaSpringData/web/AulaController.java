package holaSpringData.web;

import holaSpringData.clases.Aula;
import holaSpringData.clases.Individuo;
import holaSpringData.dto.AulaDTO;
import holaSpringData.dto.IndividuoDTO;
import holaSpringData.servicio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/aulas")
public class AulaController {

    @Autowired
    private AulaServicio aulaServicio;
    @Autowired
    private UsuarioAutenticacionServicio usuarioAutenticacionServicio;
    @Autowired
    private IndividuoServicio individuoServicio;
    @Autowired
    PdfService pdfService;


    @GetMapping
    public List<AulaDTO> listarAulas(@AuthenticationPrincipal User usuario) {
        Individuo currentUser = individuoServicio.findByNomusuario(usuario.getUsername());

        return aulaServicio.listarAulas().stream()
                .map(aula -> {
                    AulaDTO dto = new AulaDTO();
                    dto.setIdAula(aula.getId_aula());
                    dto.setNombreAula(aula.getNombreAula());
                    dto.setCursoAula(aula.getCursoAula());

                    // Verificar si el usuario actual está registrado en el aula
                    boolean esRegistrado = aula.getUsuarios().contains(currentUser);
                    dto.setRegistrado(esRegistrado);

                    // Verificar si el usuario actual es administrador del aula
                    boolean esAdmin = aula.getAdministradores().contains(currentUser);
                    dto.setEsAdmin(esAdmin);

                    return dto;
                })
                .filter(aulaDTO -> aulaDTO.isRegistrado() || aulaDTO.isEsAdmin()) // Solo retornar aulas donde el usuario está registrado o es admin
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<String> crearAula(@RequestBody Aula aula, @AuthenticationPrincipal User usuario) {
        try {
            Individuo creador = individuoServicio.findByNomusuario(usuario.getUsername());
            if (aula.getAdministradores() == null) {
                aula.setAdministradores(new ArrayList<>());
            }
            aula.getAdministradores().add(creador);
            aulaServicio.guardar(aula);
            return ResponseEntity.status(HttpStatus.CREATED).body("Aula creada con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el aula");
        }
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/editar/{id_aula}")
    public ResponseEntity<String> editarAula(@PathVariable("id_aula") Long id_aula, @RequestBody Aula aula) {
        Aula aulaExistente = aulaServicio.encontrarAula(id_aula);
        if (aulaExistente != null) {
            aulaExistente.setNombreAula(aula.getNombreAula());
            aulaExistente.setCursoAula(aula.getCursoAula());
            aulaServicio.guardar(aulaExistente);
            return ResponseEntity.ok("Aula actualizada con éxito");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aula no encontrada");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarAula(@PathVariable("id") Long id) {
        Aula aula = aulaServicio.encontrarAula(id);
        if (aula != null) {
            aulaServicio.eliminar(aula);
            return ResponseEntity.ok("Aula eliminada con éxito");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aula no encontrada");
    }

    @GetMapping("/{id_aula}")
    public ResponseEntity<?> verAula(@PathVariable("id_aula") Long id_aula, @AuthenticationPrincipal User usuario) {
        Aula aula = aulaServicio.encontrarAula(id_aula);
        Individuo currentUser = individuoServicio.findByNomusuario(usuario.getUsername());

        if (aula == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aula no encontrada");
        }

        boolean esAdmin = aula.getAdministradores().contains(currentUser);
        boolean esUsuario = aula.getUsuarios().contains(currentUser);

        if (!esAdmin && !esUsuario) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tienes permiso para ver esta aula");
        }

        AulaDTO aulaDTO = new AulaDTO();
        aulaDTO.setIdAula(aula.getId_aula());
        aulaDTO.setNombreAula(aula.getNombreAula());
        aulaDTO.setCursoAula(aula.getCursoAula());
        aulaDTO.setEsAdmin(esAdmin);
        aulaDTO.setRegistrado(esUsuario);

        return ResponseEntity.ok(aulaDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id_aula}/agregarParticipante")
    public ResponseEntity<?> agregarParticipante(
            @PathVariable("id_aula") Long id_aula,
            @RequestBody Map<String, String> payload) {
        try {
            String correo = payload.get("correo");
            String rol = payload.get("rol");

            if (correo == null || correo.isEmpty()) {
                return ResponseEntity.badRequest().body("Correo electrónico es requerido");
            }

            Individuo individuo = individuoServicio.encontrarporcorreo(correo);
            if (individuo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Determinar el rol basado en el rol del usuario, no en el parámetro
            boolean esAdministrador = individuo.getUnUsuario().getUnRol().getNombre().equals("ROLE_ADMIN");

            if (esAdministrador) {
                aulaServicio.agregarAdministrador(id_aula, individuo.getId_individuo());
            } else {
                aulaServicio.agregarUsuario(id_aula, individuo.getId_individuo());
            }

            return ResponseEntity.ok("Participante añadido correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al añadir participante");
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id_aula}/eliminarParticipante")
    public ResponseEntity<?> eliminarParticipante(
            @PathVariable("id_aula") Long id_aula,
            @RequestBody Map<String, Long> payload) {
        try {
            Long id_individuo = payload.get("id_individuo");

            if (id_individuo == null) {
                return ResponseEntity.badRequest().body("ID de individuo es requerido");
            }

            Individuo individuo = individuoServicio.encontrarPorId(id_individuo);
            if (individuo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
            }

            // Determinar el rol basado en el rol del usuario, no en un parámetro externo
            boolean esAdministrador = individuo.getUnUsuario().getUnRol().getNombre().equals("ROLE_ADMIN");

            if (esAdministrador) {
                aulaServicio.eliminarAdministrador(id_aula, id_individuo);
            } else {
                aulaServicio.eliminarUsuario(id_aula, id_individuo);
            }

            return ResponseEntity.ok("Participante eliminado correctamente");
        } catch (Exception e) {
            // Log the full error for server-side debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar participante: " + e.getMessage());
        }
    }



    @GetMapping("/{id}/participantes")
    public ResponseEntity<?> obtenerParticipantes(@PathVariable Long id) {
        Aula aula = aulaServicio.encontrarAula(id);
        if (aula == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Aula no encontrada");
        }

        // Preparamos la respuesta sin relaciones cíclicas
        Map<String, Object> response = new HashMap<>();
        response.put("administradores", convertirAIndividuoDTO(aula.getAdministradores()));
        response.put("usuarios", convertirAIndividuoDTO(aula.getUsuarios()));

        return ResponseEntity.ok(response);
    }

    private List<IndividuoDTO> convertirAIndividuoDTO(List<Individuo> individuos) {
        return individuos.stream().map(individuo -> {
            IndividuoDTO dto = new IndividuoDTO();
            dto.setId_individuo(individuo.getId_individuo()); // Add this line
            dto.setNomusuario(individuo.getUnUsuario().getNomusuario());
            dto.setContrasena(individuo.getUnUsuario().getContrasena());
            dto.setNombre(individuo.getNombre());
            dto.setApellido(individuo.getApellido());
            dto.setCorreo(individuo.getCorreo());
            dto.setRole(individuo.getUnUsuario().getUnRol().getNombre());  // O cómo sea la propiedad del rol
            return dto;
        }).collect(Collectors.toList());
    }


}
