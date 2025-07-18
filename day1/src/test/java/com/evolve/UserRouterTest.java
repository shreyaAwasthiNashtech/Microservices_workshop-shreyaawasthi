package com.evolve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.evolve.handler.UserHandler;
import com.evolve.route.UserRouter;

@WebFluxTest(controllers = UserRouter.class)
@Import({UserRouter.class, UserHandler.class})
public class UserRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void testGetUserById() {
        webTestClient.get().uri("/users/1")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(1);
    }
}
