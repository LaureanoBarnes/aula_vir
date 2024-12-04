// src/main/java/holaSpringData/servicio/EntregaActividadServicioImpl.java
package holaSpringData.servicio;

import holaSpringData.clases.EntregaActividad;
import holaSpringData.dao.EntregaActividadDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class EntregaActividadServicioImpl implements EntregaActividadServicio {

    @Autowired
    private EntregaActividadDao entregaActividadDao;

    @Autowired
    private EntregaArchivoService entregaArchivoService;  // Inyectar el nuevo servicio


    private final String BASE_UPLOAD_PATH = "upload/";


    @Override
    @Transactional(readOnly = true)
    public List<EntregaActividad> obtenerEntregasPorUsuarioYAula(Long usuarioId, Long aulaId) {
        return entregaActividadDao.findByIndividuo_UnUsuario_Id_UsuarioAndActividad_Aula_Id(usuarioId, aulaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntregaActividad> listarEntregasPorActividad(Long actividadId) {
        return entregaActividadDao.findByActividadId(actividadId);
    }

    @Override
    @Transactional(readOnly = true)
    public EntregaActividad encontrarEntregaPorId(Long entregaId) {
        return entregaActividadDao.findById(entregaId)
                .orElseThrow(() -> new RuntimeException("Entrega no encontrada"));
    }


    @Override
    @Transactional
    public EntregaActividad crearEntrega(EntregaActividad entrega, MultipartFile archivoEntregado) throws IOException {
        // Verificar si el estudiante ya ha hecho una entrega para esta actividad
        List<EntregaActividad> entregasExistentes = entregaActividadDao.findByIndividuoAndActividad(entrega.getIndividuo(), entrega.getActividad());

        EntregaActividad entregaExistente = entregasExistentes.isEmpty() ? null : entregasExistentes.get(0);

        // Si ya existe una entrega, se actualiza en lugar de crear una nueva
        if (entregaExistente != null) {
            entregaExistente.setFechaEntrega(java.time.LocalDateTime.now());

            // Si hay un nuevo archivo, se sobreescribe el archivo existente
            if (archivoEntregado != null && !archivoEntregado.isEmpty()) {
                String filePath = entregaArchivoService.saveUpload(archivoEntregado, entrega.getActividad().getAula().getId_aula(), entrega.getActividad().getId());
                entregaExistente.setArchivoEntregado(filePath);
            }

            // Guardar la entrega actualizada
            return entregaActividadDao.save(entregaExistente);
        }

        // Si no existe una entrega previa, crear una nueva
        if (archivoEntregado != null && !archivoEntregado.isEmpty()) {
            String filePath = entregaArchivoService.saveUpload(archivoEntregado, entrega.getActividad().getAula().getId_aula(), entrega.getActividad().getId());
            entrega.setArchivoEntregado(filePath);
        }

        entrega.setEstado("PENDIENTE");

        return entregaActividadDao.save(entrega);
    }

    @Override
    @Transactional
    public EntregaActividad calificarEntrega(Long entregaId, int calificacion, String feedback, String estado) {
        EntregaActividad entrega = encontrarEntregaPorId(entregaId);

        // Asignar calificación y feedback
        entrega.setCalificacion(calificacion);
        entrega.setFeedback(feedback);

        // Cambiar el estado a "CALIFICADA"
        entrega.setEstado(estado);

        entregaActividadDao.save(entrega);
        return entrega;
    }

    @Override
    @Transactional
    public void eliminarEntrega(Long entregaId) {
        entregaActividadDao.deleteById(entregaId);
    }

    private String guardarArchivo(EntregaActividad entrega, MultipartFile archivoEntregado) throws IOException {
        // Generar la ruta para guardar el archivo
        String rutaDirectorio = BASE_UPLOAD_PATH + entrega.getActividad().getAula().getId_aula()
                + "/actividades/" + entrega.getActividad().getId() + "/entregas";

        // Crear directorio si no existe
        Path directorio = Paths.get(rutaDirectorio);
        if (!Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }

        // Evitar sobreescribir archivos con el mismo nombre
        String nombreArchivo = System.currentTimeMillis() + "_" + archivoEntregado.getOriginalFilename();
        Path archivoPath = directorio.resolve(nombreArchivo);

        // Guardar el archivo de forma segura
        Files.copy(archivoEntregado.getInputStream(), archivoPath, StandardCopyOption.REPLACE_EXISTING);

        return archivoPath.toString();  // Devolver la ruta completa del archivo
    }
    @Override
    public EntregaActividad actualizarEntrega(
            Long entregaId,
            MultipartFile nuevoArchivo,
            String feedback,
            Integer calificacion,
            String estado) throws Exception {

        // Obtener la entrega actual
        EntregaActividad entrega = encontrarEntregaPorId(entregaId);

        // Manejo del archivo nuevo
        if (nuevoArchivo != null && !nuevoArchivo.isEmpty()) {
            // Si existe un archivo anterior, eliminarlo primero
            if (entrega.getArchivoEntregado() != null) {
                entregaArchivoService.deleteUpload(entrega.getArchivoEntregado());
            }

            // Guardar el nuevo archivo
            String rutaArchivo = entregaArchivoService.saveUpload(
                    nuevoArchivo,
                    entrega.getActividad().getAula().getId_aula(),
                    entrega.getActividad().getId());
            entrega.setArchivoEntregado(rutaArchivo);
        }

        // Actualizar feedback, calificación y estado si se proporcionan
        if (feedback != null) entrega.setFeedback(feedback);
        if (calificacion != null) entrega.setCalificacion(calificacion);
        if (estado != null) entrega.setEstado(estado);

        // Guardar los cambios
        return entregaActividadDao.save(entrega);
    }
    @Override
    @Transactional(readOnly = true)
    public EntregaActividad obtenerEntregaPorUsuarioYActividad(Long idUsuario, Long idActividad) {
        return entregaActividadDao.findByIndividuoIdAndActividadId(idUsuario, idActividad)
                .stream()
                .findFirst()
                .orElse(null);
    }

}
