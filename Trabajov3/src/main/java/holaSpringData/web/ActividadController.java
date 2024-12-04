package holaSpringData.web;

import holaSpringData.clases.Actividad;
import holaSpringData.clases.Aula;
import holaSpringData.clases.EntregaActividad;
import holaSpringData.clases.Individuo;
import holaSpringData.dto.ActividadDTO;
import holaSpringData.dto.EntregaActividadDTO;
import holaSpringData.servicio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("api/aulas/{id_aula}/actividades")
public class ActividadController {

    private static final Logger logger = LoggerFactory.getLogger(ActividadController.class);

    @Autowired
    private ActividadServicio actividadServicio;

    @Autowired
    private AulaServicio aulaServicio;

    @Autowired
    private EntregaActividadServicio entregaActividadServicio;


    @Autowired
    private ActividadArchivoService actividadArchivoService;

    private final UsuarioAutenticacionServicio usuarioAutenticacionServicio;

    public ActividadController(UsuarioAutenticacionServicio usuarioAutenticacionServicio) {
        this.usuarioAutenticacionServicio = usuarioAutenticacionServicio;
    }


    private ActividadDTO convertirADTO(Actividad actividad) {
        ActividadDTO dto = new ActividadDTO();
        dto.setId(actividad.getId());
        dto.setTitulo(actividad.getTitulo());
        dto.setDescripcion(actividad.getDescripcion());
        dto.setPlazoEntrega(actividad.getPlazoEntrega());
        dto.setRutaArchivo(actividad.getRutaArchivo());
        return dto;
    }

    // Listar actividades
    @GetMapping
    public ResponseEntity<List<ActividadDTO>> listarActividades(@PathVariable Long id_aula) {
        List<ActividadDTO> actividades = actividadServicio
                .listarActividadesPorAula(id_aula)
                .stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(actividades);
    }




    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ActividadDTO> crearActividad(
            @PathVariable Long id_aula,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String plazoEntrega,
            @RequestParam MultipartFile archivo) throws IOException {

        Aula aula = aulaServicio.encontrarAula(id_aula);
        Actividad nuevaActividad = new Actividad();
        nuevaActividad.setTitulo(titulo);
        nuevaActividad.setDescripcion(descripcion);
        nuevaActividad.setPlazoEntrega(LocalDateTime.parse(plazoEntrega));

        if (!archivo.isEmpty()) {
            String rutaArchivo = actividadArchivoService.saveUpload(archivo, id_aula);
            nuevaActividad.setRutaArchivo(rutaArchivo);
        }

        nuevaActividad.setAula(aula);
        actividadServicio.crearActividad(nuevaActividad);

        return ResponseEntity.ok(convertirADTO(nuevaActividad));
    }



    // Editar actividad con PUT
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id_actividad}")
    public ResponseEntity<ActividadDTO> editarActividad(
            @PathVariable Long id_aula,
            @PathVariable Long id_actividad,
            @RequestParam String titulo,
            @RequestParam String descripcion,
            @RequestParam String plazoEntrega,
            @RequestParam(required = false) MultipartFile archivo) throws IOException {

        Actividad actividadExistente = actividadServicio.encontrarActividad(id_actividad);
        if (actividadExistente == null) {
            return ResponseEntity.notFound().build();
        }

        actividadExistente.setTitulo(titulo);
        actividadExistente.setDescripcion(descripcion);
        actividadExistente.setPlazoEntrega(LocalDateTime.parse(plazoEntrega));

        if (archivo != null && !archivo.isEmpty()) {
            String rutaArchivo = actividadArchivoService.saveUpload(archivo, id_aula);
            actividadExistente.setRutaArchivo(rutaArchivo);
        }

        actividadServicio.actualizarActividad(actividadExistente);
        return ResponseEntity.ok(convertirADTO(actividadExistente));
    }
    // Eliminar actividad
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id_actividad}")
    public ResponseEntity<Void> eliminarActividad(@PathVariable Long id_aula, @PathVariable Long id_actividad) {
        actividadServicio.eliminarActividad(id_actividad);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/entregas-usuario")
    public ResponseEntity<List<EntregaActividadDTO>> obtenerEntregasDelUsuario(
            @PathVariable Long id_aula,
            @AuthenticationPrincipal User usuario) {

        // Obtener al usuario autenticado
        Individuo estudiante = usuarioAutenticacionServicio.obtenerUsuarioActual(usuario);

        // Obtener las entregas relacionadas con el usuario y el aula
        List<EntregaActividad> entregas = entregaActividadServicio.obtenerEntregasPorUsuarioYAula(
                estudiante.getUnUsuario().getId_usuario(),
                id_aula
        );

        // Convertir a DTO
        List<EntregaActividadDTO> entregasDTO = entregas.stream()
                .map(entrega -> {
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
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(entregasDTO);
    }


}
