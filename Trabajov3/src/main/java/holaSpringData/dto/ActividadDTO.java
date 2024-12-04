package holaSpringData.dto;

import java.time.LocalDateTime;

public class ActividadDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime plazoEntrega;
    private String rutaArchivo;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getPlazoEntrega() {
        return plazoEntrega;
    }

    public void setPlazoEntrega(LocalDateTime plazoEntrega) {
        this.plazoEntrega = plazoEntrega;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
