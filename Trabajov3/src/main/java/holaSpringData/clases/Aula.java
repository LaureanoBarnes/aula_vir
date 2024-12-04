package holaSpringData.clases;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;



@Data
@Entity
@Table(name = "aula")
public class Aula implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_aula;

    @NotEmpty
    @Column(name = "nombreAula")
    private String nombreAula;

    @Column(name = "cursoAula")
    @NotEmpty
    private String cursoAula;


    @JoinTable(
            name = "aula_administradores",
            joinColumns = @JoinColumn(name = "aula_id"),
            inverseJoinColumns = @JoinColumn(name = "individuo_id")
    )

    @ManyToMany
    private List<Individuo> administradores;


    @JoinTable(
            name = "aula_usuarios",
            joinColumns = @JoinColumn(name = "aula_id"),
            inverseJoinColumns = @JoinColumn(name = "individuo_id")
    )
    @ManyToMany
    private List<Individuo> usuarios;

    public Long getId_aula() {
        return id_aula;
    }

    public void setId_aula(Long id_aula) {
        this.id_aula = id_aula;
    }

    public @NotEmpty String getNombreAula() {
        return nombreAula;
    }

    public void setNombreAula(@NotEmpty String nombreAula) {
        this.nombreAula = nombreAula;
    }

    public @NotEmpty String getCursoAula() {
        return cursoAula;
    }

    public void setCursoAula(@NotEmpty String cursoAula) {
        this.cursoAula = cursoAula;
    }

    public List<Individuo> getAdministradores() {
        return administradores;
    }

    public void setAdministradores(List<Individuo> administradores) {
        this.administradores = administradores;
    }

    public List<Individuo> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<Individuo> usuarios) {
        this.usuarios = usuarios;
    }
}