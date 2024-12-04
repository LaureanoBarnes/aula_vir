package holaSpringData.servicio;

import holaSpringData.clases.Archivos;
import holaSpringData.clases.Individuo;
import holaSpringData.dao.AulaDao;
import holaSpringData.dao.IndividuoDao;
import holaSpringData.dao.UsuarioDao;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@Service
public class IndividuoServicioImp implements IndividuoServicio{


    @Autowired
    private IndividuoDao individuoDao;
    @Autowired
    private AulaDao aulaDao;
    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public List<Individuo> listaindividuos() {
        return (List<Individuo>) individuoDao.findAll();
    }

    @Override
    @Transactional
    public void salvar(Individuo individuo) {
        individuoDao.save(individuo);
    }

    @Override
    @Transactional
    public void borrar(Individuo individuo) {
        individuoDao.deleteById(individuo.getId_individuo());

    }

    @Override
    @Transactional(readOnly = true)
    public Individuo localizarIndividuo(Individuo individuo) {
        return individuoDao.findById(individuo.getId_individuo()).orElse(null);

    }

    @Override
    @Transactional(readOnly = true)
    public Individuo findByNomusuario(String nomusuario) {
        return individuoDao.findByUnUsuarioNomusuario(nomusuario);
    }


    @Override
    public Individuo encontrarPorId(Long id) {
        return individuoDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Individuo no encontrado con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean correoExiste(String correo) {
        try {
            // Lógica para verificar si el correo existe
            return individuoDao.findByCorreo(correo) != null; // verifica duplicados
        } catch (Exception e) {
            // Manejo de excepciones
            throw new RuntimeException("Error al verificar el correo", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Individuo encontrarporcorreo(String correo){
        return individuoDao.findByCorreo(correo);
    }
}
