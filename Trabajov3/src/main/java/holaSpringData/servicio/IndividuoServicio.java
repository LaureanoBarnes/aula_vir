package holaSpringData.servicio;

import holaSpringData.clases.Individuo;

import java.util.List;
import java.util.Optional;

public interface IndividuoServicio {

    public List<Individuo> listaindividuos();

    public void salvar(Individuo individuo);

    public void borrar(Individuo individuo);

    public Individuo localizarIndividuo(Individuo individuo);

    public Individuo findByNomusuario(String nomusuario);
    Individuo encontrarPorId(Long id);

    boolean correoExiste(String correo);// nuevo método

    public Individuo encontrarporcorreo(String correo);

}
