
package holaSpringData.dao;
import holaSpringData.clases.Individuo;
import org.springframework.data.repository.CrudRepository;

public interface IndividuoDao extends CrudRepository<Individuo, Long> {
    Individuo findByUnUsuarioNomusuario(String nomusuario);
    Individuo findByCorreo(String correo); // método para verificar duplicado de correo

}
