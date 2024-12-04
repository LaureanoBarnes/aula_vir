package holaSpringData.dto;

public class ForoDTO {
    private Long id;
    private String title;
    private String description;
    private String author;
    private String createdAt;
    private String titulo;
    private String descripcion;
    private String autor;
    private String fechaCreacion;

    public ForoDTO(Long id, String title, String description, String author, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.createdAt = createdAt;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.autor = autor;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getAutor() { return autor; }
    public String getFechaCreacion() { return fechaCreacion; }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
