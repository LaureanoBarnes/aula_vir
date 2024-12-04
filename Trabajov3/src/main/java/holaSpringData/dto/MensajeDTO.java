package holaSpringData.dto;

import holaSpringData.clases.Mensaje;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MensajeDTO {
    private Long id;
    private String contenido;
    private String autor;
    private LocalDateTime fechaCreacion; // Cambiado de String a LocalDateTime
    private List<ReaccionDTO> reacciones;
    private Long mensajePadreId;
    private List<MensajeDTO> respuestas;
    private boolean hasLiked;


    public MensajeDTO(Mensaje mensaje) {
        this.id = mensaje.getId();
        this.contenido = mensaje.getContenido();
        this.autor = (mensaje.getAutor() != null)
                ? mensaje.getAutor().getNombre() + " " + mensaje.getAutor().getApellido()
                : "Autor desconocido";
        this.fechaCreacion = mensaje.getFechaCreacion(); // Guardamos directamente el LocalDateTime
        this.mensajePadreId = mensaje.getMensajePadre() != null ? mensaje.getMensajePadre().getId() : null;

        // Convertir reacciones a DTO
        this.reacciones = (mensaje.getReacciones() != null)
                ? mensaje.getReacciones().stream()
                .map(ReaccionDTO::new)
                .collect(Collectors.toList())
                : List.of();

        // Inicializar la lista de respuestas vacía
        this.respuestas = new ArrayList<>();
    }

    public boolean isHasLiked() {
        return hasLiked;
    }

    public void setHasLiked(boolean hasLiked) {
        this.hasLiked = hasLiked;
    }
    // Getters y setters actualizados
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ReaccionDTO> getReacciones() {
        return reacciones;
    }

    public void setReacciones(List<ReaccionDTO> reacciones) {
        this.reacciones = reacciones;
    }

    public Long getMensajePadreId() {
        return mensajePadreId;
    }

    public void setMensajePadreId(Long mensajePadreId) {
        this.mensajePadreId = mensajePadreId;
    }

    public List<MensajeDTO> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<MensajeDTO> respuestas) {
        this.respuestas = respuestas;
    }
}
