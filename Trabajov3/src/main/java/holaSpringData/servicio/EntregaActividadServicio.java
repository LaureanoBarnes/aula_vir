// src/main/java/holaSpringData/servicio/EntregaActividadServicio.java
package holaSpringData.servicio;

import holaSpringData.clases.EntregaActividad;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface EntregaActividadServicio {

    List<EntregaActividad> listarEntregasPorActividad(Long actividadId);

    EntregaActividad encontrarEntregaPorId(Long entregaId);

    EntregaActividad crearEntrega(EntregaActividad entrega, MultipartFile archivoEntregado) throws Exception;

    void eliminarEntrega(Long entregaId);

    EntregaActividad calificarEntrega(Long entregaId, int calificacion, String feedback, String estado);

    EntregaActividad actualizarEntrega(Long entregaId, MultipartFile nuevoArchivo, String feedback, Integer calificacion, String estado) throws Exception;


    EntregaActividad obtenerEntregaPorUsuarioYActividad(Long idUsuario, Long idActividad);

    List<EntregaActividad> obtenerEntregasPorUsuarioYAula(Long usuarioId, Long aulaId);

}

