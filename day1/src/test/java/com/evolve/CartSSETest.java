package com.evolve;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.evolve.springbootapp.SpringBootApp;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest(classes = SpringBootApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CartSSETest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webTestClient;

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:7.0")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void overrideRedisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", () -> redis.getMappedPort(6379));
    }

    @Test
    public void testCartSSEStream() {
        Flux<String> eventStream = webTestClient.get()
            .uri("http://localhost:" + port + "/cart/stream")
            .accept(MediaType.TEXT_EVENT_STREAM)
            .exchange()
            .expectStatus().isOk()
            .returnResult(String.class)
            .getResponseBody();

        StepVerifier.create(eventStream)
            .thenConsumeWhile(msg -> {
                System.out.println("Received SSE: " + msg);
                return true;
            })
            .thenCancel()
            .verify();
    }
}
