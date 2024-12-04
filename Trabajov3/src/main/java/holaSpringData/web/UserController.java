package holaSpringData.web;

import holaSpringData.clases.Individuo;
import holaSpringData.clases.Rol;
import holaSpringData.clases.Usuario;
import holaSpringData.dto.IndividuoDTO;
import holaSpringData.servicio.IndividuoServicio;
import holaSpringData.servicio.RolServicio;
import holaSpringData.servicio.UsuarioServicio;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final IndividuoServicio individuoServicio;
    private final UsuarioServicio usuarioServicio;
    private final RolServicio rolServicio;

    public UserController(IndividuoServicio individuoServicio, UsuarioServicio usuarioServicio, RolServicio rolServicio) {
        this.individuoServicio = individuoServicio;
        this.usuarioServicio = usuarioServicio;
        this.rolServicio = rolServicio;
    }

    // Obtener lista de roles
    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> obtenerRoles() {
        return ResponseEntity.ok(rolServicio.listaRol());
    }
    @GetMapping("/existe-nombre-usuario")
    public ResponseEntity<Boolean> existeNombreUsuario(@RequestParam String nomusuario) {
        boolean existe = usuarioServicio.usuarioExiste(nomusuario);
        return ResponseEntity.ok(existe);
    }

    @GetMapping("/existe-correo")
    public ResponseEntity<Boolean> existeCorreo(@RequestParam String correo) {
        try {
            boolean existe = individuoServicio.correoExiste(correo);
            return ResponseEntity.ok(existe);
        } catch (Exception e) {
            e.printStackTrace(); // Esto imprimirá la traza del error en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
        }
    }   

    @PostMapping("/registro")
    public ResponseEntity<String> registrarUsuario(@RequestBody IndividuoDTO individuoDTO) {
        try {
            // Validar duplicados de nombre de usuario y correo
            if (usuarioServicio.usuarioExiste(individuoDTO.getNomusuario())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Nombre de usuario ya existe");
            }
            if (individuoServicio.correoExiste(individuoDTO.getCorreo())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Correo ya registrado");
            }

            Rol rol = rolServicio.localizarRol(individuoDTO.getRole());
            if (rol == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol no válido");
            }

            Usuario usuario = new Usuario();
            usuario.setNomusuario(individuoDTO.getNomusuario());
            usuario.setContrasena(individuoDTO.getContrasena());
            usuario.setUnRol(rol);
            usuarioServicio.salvar(usuario);

            Individuo individuo = new Individuo();
            individuo.setNombre(individuoDTO.getNombre());
            individuo.setApellido(individuoDTO.getApellido());
            individuo.setCorreo(individuoDTO.getCorreo());
            individuo.setEdad(String.valueOf(individuoDTO.getEdad()));
            individuo.setUnUsuario(usuario);
            individuoServicio.salvar(individuo);

            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error en el registro");
        }
    }
}
