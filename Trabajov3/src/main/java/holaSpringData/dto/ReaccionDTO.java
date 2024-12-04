package holaSpringData.dto;

import holaSpringData.clases.Reaccion;

public class ReaccionDTO {
    private Long id;
    private String tipo;
    private String usuario;

    public ReaccionDTO(Reaccion reaccion) {
        this.id = reaccion.getId();
        this.tipo = reaccion.getTipo();
        this.usuario = reaccion.getIndividuo().getNombre() + " " + reaccion.getIndividuo().getApellido();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
// getters y setters
}
