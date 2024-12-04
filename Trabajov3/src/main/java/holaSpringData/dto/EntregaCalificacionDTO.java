package holaSpringData.dto;

public class EntregaCalificacionDTO {
    private String estado;
    private int calificacion;
    private String feedback;

    // Getters y setters
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
