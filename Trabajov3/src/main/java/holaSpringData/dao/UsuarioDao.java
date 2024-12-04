package holaSpringData.dao;

import holaSpringData.clases.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UsuarioDao extends CrudRepository<Usuario, Long> {
    Usuario findByNomusuario(String nomusuario);


    @Query("SELECT u FROM Usuario u JOIN u.unIndividuo i WHERE u.nomusuario = :nomusuario OR i.correo = :correo")
    Usuario findByNomusuarioOrCorreo(@Param("nomusuario") String nomusuario, @Param("correo") String correo);

}