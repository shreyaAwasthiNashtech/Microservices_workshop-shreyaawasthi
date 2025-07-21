package com.evolve;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketTest {

    @LocalServerPort
    private int port;

    @Test
    public void testWebSocketConnection() throws Exception {
        WebSocketClient client = new ReactorNettyWebSocketClient();
        client.execute(
            URI.create("ws://localhost:" + port + "/ws/cart"),
            session -> session.send(Mono.just(session.textMessage("test")))
                               .thenMany(session.receive().take(1))
                               .then())
            .block();
    }
}

