package holaSpringData.dao;

import holaSpringData.clases.Archivos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArchivoDao extends JpaRepository <Archivos, Long> {
    List<Archivos> findByAulaId(Long aulaId);
}
