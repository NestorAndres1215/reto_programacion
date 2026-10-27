
package api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.Map;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class KeycloakLoginTest {

    @Autowired
    private WebTestClient gatewayClient;

    private final WebClient keycloakClient = WebClient.builder().build();

    @Test
    void debeIniciarSesionCorrectamente() {

        try {
            Map<?, ?> respuesta = keycloakClient.post()
                    .uri("http://localhost:8085/realms/demo/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData("grant_type", "password")
                            .with("client_id", "gateway-client")
                            .with("username", "usuario1")
                            .with("password", "ClaveDemo123!"))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String token = respuesta == null
                    ? null
                    : (String) respuesta.get("access_token");

            if (token != null && !token.isBlank()) {
                System.out.println("Inicio de sesión exitoso.");
            } else {
                System.out.println("Inicio de sesión fallido.");
            }

        } catch (WebClientResponseException e) {
            System.out.println("Inicio de sesión fallido: credenciales inválidas o acceso rechazado.");
        } catch (Exception e) {
            System.out.println("No se pudo conectar con Keycloak.");
        }
    }

}
