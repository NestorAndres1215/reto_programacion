
package api_gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SecurityConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    private WebTestClient webTestClient;

    @BeforeEach
    void configurarCliente() {
        webTestClient = WebTestClient
                .bindToApplicationContext(applicationContext)
                .apply(springSecurity())
                .configureClient()
                .build();
    }

    @Test
    void debePermitirAccesoConJwtValido() {
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt
                        .subject("usuario-test")
                        .claim("preferred_username", "usuario")))
                .get()
                .uri("/api/auth/hola")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.autenticado").isEqualTo(true)
                .jsonPath("$.mensaje").isEqualTo("Hola mundo");
    }

    @Test
    void debeRechazarPeticionSinToken() {
        webTestClient
                .get()
                .uri("/api/auth/hola")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void debeRechazarTokenInvalido() {
        webTestClient
                .get()
                .uri("/api/auth/hola")
                .headers(headers -> headers.setBearerAuth("token-invalido"))
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
