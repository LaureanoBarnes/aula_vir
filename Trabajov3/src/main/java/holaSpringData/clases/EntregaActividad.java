// src/main/java/holaSpringData/clases/EntregaActividad.java
package holaSpringData.clases;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "entrega_actividad")
public class EntregaActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;

    @ManyToOne
    @JoinColumn(name = "individuo_id", nullable = false)
    private Individuo individuo;  // Estudiante que realiza la entrega

    private String archivoEntregado;  // Ruta al archivo entregado
    private LocalDateTime fechaEntrega;
    private String estado;  // PENDIENTE, EN_REVISION, CALIFICADA

    private int calificacion;  // Calificación de 1-10 o puede ser 0 (sin calificación)
    private String feedback;  // Feedback de al menos 500 caracteres

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Individuo getIndividuo() {
        return individuo;
    }

    public void setIndividuo(Individuo individuo) {
        this.individuo = individuo;
    }

    public String getArchivoEntregado() {
        return archivoEntregado;
    }

    public void setArchivoEntregado(String archivoEntregado) {
        this.archivoEntregado = archivoEntregado;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}