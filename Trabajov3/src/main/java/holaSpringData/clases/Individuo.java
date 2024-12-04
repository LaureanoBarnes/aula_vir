package holaSpringData.clases;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name= "individuo")
public class Individuo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_individuo;

    @NotEmpty
    private String nombre;
    @NotEmpty
    private String apellido;

    @NotEmpty
    @Email(regexp = ".+@.+\\..+",message = "Email debe ser válido")
    private String correo;
    @NotEmpty
    private String edad;

    private String telefono;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_usuario")
    private Usuario unUsuario;


    @ManyToMany
    @JoinTable(
            name = "aula_usuarios",
            joinColumns = @JoinColumn(name = "individuo_id"),
            inverseJoinColumns = @JoinColumn(name = "aula_id")
    )
    private List<Aula> aulas;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public Long getId_individuo() {
        return id_individuo;
    }

    public void setId_individuo(Long id_individuo) {
        this.id_individuo = id_individuo;
    }

    public @NotEmpty String getNombre() {
        return nombre;
    }

    public void setNombre(@NotEmpty String nombre) {
        this.nombre = nombre;
    }

    public @NotEmpty String getApellido() {
        return apellido;
    }

    public void setApellido(@NotEmpty String apellido) {
        this.apellido = apellido;
    }

    public @NotEmpty @Email(regexp = ".+@.+\\..+", message = "Email debe ser válido") String getCorreo() {
        return correo;
    }

    public void setCorreo(@NotEmpty @Email(regexp = ".+@.+\\..+", message = "Email debe ser válido") String correo) {
        this.correo = correo;
    }

    public @NotEmpty String getEdad() {
        return edad;
    }

    public void setEdad(@NotEmpty String edad) {
        this.edad = edad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Usuario getUnUsuario() {
        return unUsuario;
    }

    public void setUnUsuario(Usuario unUsuario) {
        this.unUsuario = unUsuario;
    }

    public List<Aula> getAulas() {
        return aulas;
    }

    public void setAulas(List<Aula> aulas) {
        this.aulas = aulas;
    }
}
