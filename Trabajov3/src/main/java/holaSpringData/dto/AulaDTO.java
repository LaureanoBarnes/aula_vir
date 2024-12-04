package holaSpringData.dto;




public class AulaDTO {
    private Long idAula;
    private String nombreAula;
    private String cursoAula;
    private boolean registrado;
    private boolean esAdmin;

    // Getters y Setters


    public Long getIdAula() {
        return idAula;
    }

    public void setIdAula(Long idAula) {
        this.idAula = idAula;
    }

    public String getNombreAula() {
        return nombreAula;
    }

    public void setNombreAula(String nombreAula) {
        this.nombreAula = nombreAula;
    }

    public String getCursoAula() {
        return cursoAula;
    }

    public void setCursoAula(String cursoAula) {
        this.cursoAula = cursoAula;
    }

    public boolean isRegistrado() {
        return registrado;
    }

    public void setRegistrado(boolean registrado) {
        this.registrado = registrado;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }
}
