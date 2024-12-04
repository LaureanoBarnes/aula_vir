package holaSpringData.dto;

import lombok.Data;

@Data
public class IndividuoDTO {
    private long id_individuo;
    private String nomusuario;
    private String contrasena;
    private String nombre;
    private String apellido;
    private String correo;
    private String role;
    private Integer edad;

    public long getId_individuo() {
        return id_individuo;
    }

    public void setId_individuo(long id_individuo) {
        this.id_individuo = id_individuo;
    }

    public String getNomusuario() {
        return nomusuario;
    }

    public void setNomusuario(String nomusuario) {
        this.nomusuario = nomusuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }
}
