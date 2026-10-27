
package api_gateway;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HolaMundoController {

    @GetMapping("/api/auth/hola")
    public Map<String, Object> holaMundo(Authentication authentication) {
        System.out.println(">>> HolaMundoController fue ejecutado");
        System.out.println(">>> Authentication: " + authentication);

        return Map.of(
                "mensaje", "Hola mundo",
                "autenticado", authentication != null,
                "usuario", authentication != null
                        ? authentication.getName()
                        : "anónimo"
        );
    }
}
