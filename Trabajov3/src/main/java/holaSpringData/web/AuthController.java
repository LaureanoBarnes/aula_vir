package holaSpringData.web;

import holaSpringData.clases.Usuario;
import holaSpringData.dao.UsuarioDao;
import holaSpringData.dto.LoginRequest;
import holaSpringData.servicio.UsuarioServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

@Autowired
private UsuarioServicio usuarioServicio;
@Autowired
private UsuarioDao usuarioDao;


    /**
     * Endpoint para obtener el usuario autenticado
     */
    @GetMapping("/user")
    public ResponseEntity<String> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication.getPrincipal().equals("anonymousUser"))) {
            return ResponseEntity.ok("Usuario autenticado: " + authentication.getName());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
    }
    @GetMapping("/role")
    public ResponseEntity<String> getUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            String role = authentication.getAuthorities().stream()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .findFirst()
                    .orElse("ROLE_USER");
            return ResponseEntity.ok(role);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
    }



    /**
     * Endpoint para cerrar sesión
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        // Borrar la cookie de sesión JSESSIONID
        response.setHeader(HttpHeaders.SET_COOKIE, "JSESSIONID=; Path=/; HttpOnly; SameSite=None; Max-Age=0;");
        return ResponseEntity.ok("Logout successful");
    }
    @GetMapping("/perfil")
    public ResponseEntity<?> getAuthenticatedUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        String username = authentication.getName();  // Obtener el nombre de usuario autenticado
        Usuario usuario = usuarioDao.findByNomusuario(username);

        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }

        // Construir el objeto de respuesta con nombre y apellido
        var userProfile = new UserProfileResponse(usuario.getUnIndividuo().getNombre(), usuario.getUnIndividuo().getApellido());

        return ResponseEntity.ok(userProfile);
    }

    // Clase de respuesta para encapsular nombre y apellido
    public static class UserProfileResponse {
        private String nombre;
        private String apellido;

        public UserProfileResponse(String nombre, String apellido) {
            this.nombre = nombre;
            this.apellido = apellido;
        }

        public String getNombre() { return nombre; }
        public String getApellido() { return apellido; }
    }
}
