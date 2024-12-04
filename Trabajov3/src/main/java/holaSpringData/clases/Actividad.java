// src/main/java/holaSpringData/clases/Actividad.java
package holaSpringData.clases;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "actividades")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    private String titulo;
    private String descripcion;


    private LocalDateTime fechaCreacion = LocalDateTime.now();
    private LocalDateTime plazoEntrega;

    private String estado;  // PENDIENTE, EN_REVISION, CALIFICADA

//    private String archivo;  // Ruta al archivo opcional


    @OneToMany(mappedBy = "actividad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntregaActividad> entregas;
    private String rutaArchivo;



    // Getters y setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getPlazoEntrega() {
        return plazoEntrega;
    }

    public void setPlazoEntrega(LocalDateTime plazoEntrega) {
        this.plazoEntrega = plazoEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<EntregaActividad> getEntregas() {
        return entregas;
    }

    public void setEntregas(List<EntregaActividad> entregas) {
        this.entregas = entregas;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }
}
