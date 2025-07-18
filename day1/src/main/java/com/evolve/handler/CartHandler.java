package com.evolve.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import com.evolve.model.Cart;
import com.evolve.repository.CartRepository;

import reactor.core.publisher.Mono;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@Component
public class CartHandler {

    private final CartRepository cartRepository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

     public CartHandler(CartRepository cartRepository, ReactiveRedisTemplate<String, String> redisTemplate) {
        this.cartRepository = cartRepository;
        this.redisTemplate = redisTemplate;
    }

    public Mono<ServerResponse> getAll(ServerRequest req) {
        return ServerResponse.ok().body(cartRepository.findAll(), Cart.class);
    }

    public Mono<ServerResponse> getById(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return cartRepository.findById(id)
                .flatMap(cart -> ServerResponse.ok().bodyValue(cart))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getByUserId(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("userId"));
        return ServerResponse.ok().body(cartRepository.findAllByUserId(userId), Cart.class);
    }

    public Mono<ServerResponse> create(ServerRequest req) {
        Long userId = Long.parseLong(req.pathVariable("userId"));
        return req.bodyToMono(Cart.class)
                .map(cart -> {
                    cart.setUserId(userId);
                    return cart;
                })
                .flatMap(cartRepository::save)
                .flatMap(saved -> {
                    redisTemplate.convertAndSend("cart-events", "Cart created for user " + userId).subscribe();
                    return ServerResponse.ok().bodyValue(saved);
                });
    }

     public Mono<ServerResponse> update(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return req.bodyToMono(Cart.class).flatMap(incoming ->
            cartRepository.findById(id).flatMap(existing -> {
                existing.setStatus(incoming.getStatus());
                return cartRepository.save(existing);
            }).flatMap(updated -> {
                redisTemplate.convertAndSend("cart-events", "Cart updated with id " + id).subscribe();
                return ServerResponse.ok().bodyValue(updated);
            }).switchIfEmpty(ServerResponse.notFound().build())
        );
    }

    public Mono<ServerResponse> delete(ServerRequest req) {
        Long id = Long.parseLong(req.pathVariable("id"));
        return cartRepository.deleteById(id)
                .then(ServerResponse.noContent().build());
    }
}


