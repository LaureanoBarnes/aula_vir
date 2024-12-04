package holaSpringData.clases;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @Column(unique = true)
    @NotEmpty
    private String nomusuario;
    @NotEmpty
    private String contrasena;
    @ManyToOne
    @JoinColumn(name="rol_id_rol")
    private Rol unRol;

    @OneToOne(mappedBy = "unUsuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Individuo unIndividuo;

    public Long getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Long id_usuario) {
        this.id_usuario = id_usuario;
    }

    public @NotEmpty String getNomusuario() {
        return nomusuario;
    }

    public void setNomusuario(@NotEmpty String nomusuario) {
        this.nomusuario = nomusuario;
    }

    public @NotEmpty String getContrasena() {
        return contrasena;
    }

    public void setContrasena(@NotEmpty String contrasena) {
        this.contrasena = contrasena;
    }

    public Rol getUnRol() {
        return unRol;
    }

    public void setUnRol(Rol unRol) {
        this.unRol = unRol;
    }

    public Individuo getUnIndividuo() {
        return unIndividuo;
    }

    public void setUnIndividuo(Individuo unIndividuo) {
        this.unIndividuo = unIndividuo;
    }
}
