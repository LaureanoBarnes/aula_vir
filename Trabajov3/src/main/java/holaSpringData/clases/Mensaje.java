// src/main/java/holaSpringData/clases/Mensaje.java
package holaSpringData.clases;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "foro_id", nullable = false)
    private Foro foro;

    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Individuo autor;

    @ManyToOne
    @JoinColumn(name = "mensaje_padre_id")
    private Mensaje mensajePadre; // Para respuestas anidadas

    @OneToMany(mappedBy = "mensajePadre", cascade = CascadeType.ALL)
    private List<Mensaje> respuestas = new ArrayList<>();

    private String contenido;
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Edicion> ediciones;

    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaccion> reacciones;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Foro getForo() {
        return foro;
    }

    public void setForo(Foro foro) {
        this.foro = foro;
    }

    public Individuo getAutor() {
        return autor;
    }

    public void setAutor(Individuo autor) {
        this.autor = autor;
    }

    public Mensaje getMensajePadre() {
        return mensajePadre;
    }

    public void setMensajePadre(Mensaje mensajePadre) {
        this.mensajePadre = mensajePadre;
    }

    public List<Mensaje> getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(List<Mensaje> respuestas) {
        this.respuestas = respuestas;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<Edicion> getEdiciones() {
        return ediciones;
    }

    public void setEdiciones(List<Edicion> ediciones) {
        this.ediciones = ediciones;
    }

    public List<Reaccion> getReacciones() {
        return reacciones;
    }

    public void setReacciones(List<Reaccion> reacciones) {
        this.reacciones = reacciones;
    }
}
