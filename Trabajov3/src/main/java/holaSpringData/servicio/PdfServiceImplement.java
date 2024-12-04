package holaSpringData.servicio;

import holaSpringData.clases.Archivos;
import holaSpringData.dao.ArchivoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfServiceImplement implements PdfService {

    @Autowired
    private ArchivoDao archivodao;
    @Autowired
    private UploadService uploadService;

    String url = "/upload/";

    @Override
    public Archivos save(Archivos archivo, MultipartFile file, Long idAula) throws IOException {
        String filePath = uploadService.saveUpload(file, idAula);
        archivo.setNombre(filePath);
        archivo.setAulaId(idAula);  // Asegurar que se asocie el aulaId
        return archivodao.save(archivo);
    }

    @Override
    public List<Archivos> getArchivos(Long idAula) {
        return archivodao.findByAulaId(idAula).stream()
                .map(archivo -> {
                    archivo.setNombre(url + archivo.getNombre());  // Asegura que la URL incluya /upload/
                    return archivo;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Archivos get(Long id) {
        Archivos archivo = archivodao.findById(id).orElse(null);
        if (archivo != null) {
            archivo.setNombre(url + archivo.getNombre());
        }
        return archivo;
    }

    @Override
    public Archivos update(Long id, Archivos archivoExistente, MultipartFile file, Long idAula) throws IOException {
        archivoExistente.setId(id);
        String filePath = uploadService.saveUpload(file, idAula);
        archivoExistente.setNombre(filePath);
        archivoExistente.setAulaId(idAula);
        return archivodao.save(archivoExistente);
    }

    @Override
    public void delete(Long id) throws IOException {
        Archivos archivo = archivodao.findById(id).orElse(null);
        if (archivo != null) {
            uploadService.deleteUpload(archivo.getNombre());
            archivodao.delete(archivo);
        }
    }
}
