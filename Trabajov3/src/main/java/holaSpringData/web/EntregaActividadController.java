// src/main/java/holaSpringData/web/EntregaActividadController.java
package holaSpringData.web;

import holaSpringData.clases.Actividad;
import holaSpringData.clases.Aula;
import holaSpringData.clases.EntregaActividad;
import holaSpringData.clases.Individuo;
import holaSpringData.dto.EntregaActividadDTO;
import holaSpringData.dto.EntregaCalificacionDTO;
import holaSpringData.servicio.ActividadServicio;
import holaSpringData.servicio.AulaServicio;
import holaSpringData.servicio.EntregaActividadServicio;
import holaSpringData.servicio.UsuarioAutenticacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/aulas/{id_aula}/actividades/{id_actividad}/entregas")
public class EntregaActividadController {

    private final EntregaActividadServicio entregaActividadServicio;
    private final UsuarioAutenticacionServicio usuarioAutenticacionServicio;
    private final ActividadServicio actividadServicio;

    @Autowired
    public EntregaActividadController(
            EntregaActividadServicio entregaActividadServicio,
            UsuarioAutenticacionServicio usuarioAutenticacionServicio,
            ActividadServicio actividadServicio) {
        this.entregaActividadServicio = entregaActividadServicio;
        this.usuarioAutenticacionServicio = usuarioAutenticacionServicio;
        this.actividadServicio = actividadServicio;
    }

    private EntregaActividadDTO convertirADTO(EntregaActividad entrega) {
        EntregaActividadDTO dto = new EntregaActividadDTO();
        dto.setId(entrega.getId());
        dto.setIdActividad(entrega.getActividad().getId());
        dto.setEstudianteNombre(entrega.getIndividuo().getNombre() + " " + entrega.getIndividuo().getApellido());
        dto.setArchivoEntregado(entrega.getArchivoEntregado());
        dto.setFechaEntrega(entrega.getFechaEntrega());
        dto.setEstado(entrega.getEstado());
        dto.setCalificacion(entrega.getCalificacion());
        dto.setFeedback(entrega.getFeedback());
        return dto;
    }

    @PostMapping
    public ResponseEntity<EntregaActividadDTO> crearEntrega(
            @PathVariable Long id_aula,
            @PathVariable Long id_actividad,
            @RequestParam("archivo") MultipartFile archivo,
            @AuthenticationPrincipal User usuario) throws Exception {

        Individuo estudiante = usuarioAutenticacionServicio.obtenerUsuarioActual(usuario);
        Actividad actividad = actividadServicio.encontrarActividad(id_actividad);

        if (actividad == null) {
            return ResponseEntity.notFound().build();
        }

        // Verificar si la fecha de entrega está dentro del plazo
        if (LocalDateTime.now().isAfter(actividad.getPlazoEntrega())) {
            return ResponseEntity.badRequest().build();
        }

        EntregaActividad entrega = new EntregaActividad();
        entrega.setIndividuo(estudiante);
        entrega.setActividad(actividad);
        entrega.setFechaEntrega(LocalDateTime.now());
        entrega.setEstado("ENTREGADO");

        EntregaActividad entregaGuardada = entregaActividadServicio.crearEntrega(entrega, archivo);
        return ResponseEntity.ok(convertirADTO(entregaGuardada));
    }

    @GetMapping
    public ResponseEntity<List<EntregaActividadDTO>> listarEntregas(
            @PathVariable Long id_actividad,
            @AuthenticationPrincipal User usuario) {

        List<EntregaActividad> entregas = entregaActividadServicio.listarEntregasPorActividad(id_actividad);
        List<EntregaActividadDTO> entregasDTO = entregas.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(entregasDTO);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{entrega_id}")
    public ResponseEntity<EntregaActividadDTO> calificarEntrega(
            @PathVariable Long id_aula,
            @PathVariable Long id_actividad,
            @PathVariable Long entrega_id,
            @RequestBody EntregaCalificacionDTO request) {


        // Validar el estado
        if (!List.of("APROBADO", "RECHAZADO", "PENDIENTE_REENTREGA").contains(request.getEstado())) {
            return ResponseEntity.badRequest().build();
        }

        // Validar la calificación
        if (request.getCalificacion() < 1 || request.getCalificacion() > 10) {
            return ResponseEntity.badRequest().build();
        }

        EntregaActividad entregaCalificada = entregaActividadServicio.calificarEntrega(
                entrega_id,
                request.getCalificacion(),
                request.getFeedback(),
                request.getEstado()
        );

        return ResponseEntity.ok(convertirADTO(entregaCalificada));
    }
    @GetMapping("/mi-entrega")
    public ResponseEntity<EntregaActividadDTO> obtenerEntregaUsuario(
            @PathVariable Long id_actividad,
            @AuthenticationPrincipal User usuario) {

        Individuo estudiante = usuarioAutenticacionServicio.obtenerUsuarioActual(usuario);
        EntregaActividad entrega = entregaActividadServicio.obtenerEntregaPorUsuarioYActividad(estudiante.getUnUsuario().getId_usuario(), id_actividad);

        if (entrega == null) {
            EntregaActividadDTO entregaDTO = new EntregaActividadDTO();
            entregaDTO.setEstado("SIN_ENTREGA");
            return ResponseEntity.ok(entregaDTO);
        }

        return ResponseEntity.ok(convertirADTO(entrega));
    }
}