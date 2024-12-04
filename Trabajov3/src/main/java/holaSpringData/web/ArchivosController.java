package holaSpringData.web;

import holaSpringData.clases.Archivos;
import holaSpringData.servicio.PdfService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchivosController {

    private final PdfService pdfService;

    public ArchivosController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/archivo")
    public ResponseEntity<String> saveArchivos(
            @RequestParam MultipartFile file,
            @RequestParam String categoria,
            @RequestParam Long aulaId) {

        try {
            Archivos archivo = new Archivos();
            archivo.setNombre(file.getOriginalFilename());
            archivo.setCategoria(categoria);
            archivo.setAulaId(aulaId);

            pdfService.save(archivo, file, aulaId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Archivo subido correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir el archivo: " + e.getMessage());
        }
    }


    @GetMapping("/archivo/{aulaId}")
    public ResponseEntity<List<Archivos>> getArchivosPorAula(@PathVariable Long aulaId) {
        List<Archivos> archivos = pdfService.getArchivos(aulaId);
        return ResponseEntity.ok(archivos);
    }

    @GetMapping("/archivo/detalle/{id}")
    public ResponseEntity<Archivos> getArchivo(@PathVariable Long id) {
        Archivos archivo = pdfService.get(id);
        if (archivo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(archivo);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/archivo/{id}")
    public ResponseEntity<String> updateArchivo(
            @PathVariable Long id,
            @RequestParam Long aulaId,
            @RequestParam MultipartFile file,
            @RequestParam String categoria) {
        try {
            Archivos archivoExistente = pdfService.get(id);
            if (archivoExistente == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Archivo no encontrado");
            }
            archivoExistente.setCategoria(categoria);
            pdfService.update(id, archivoExistente, file, aulaId);
            return ResponseEntity.ok("Archivo actualizado correctamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar archivo: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/archivo/{id}")
    public ResponseEntity<String> deleteArchivo(@PathVariable Long id) {
        try {
            pdfService.delete(id);
            return ResponseEntity.ok("Archivo eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar archivo: " + e.getMessage());
        }
    }
}
