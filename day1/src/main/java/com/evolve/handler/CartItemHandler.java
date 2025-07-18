package com.evolve.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;

import com.evolve.dto.CartItemWithProduct;
import com.evolve.model.CartItem;
import com.evolve.repository.CartItemRepository;
import com.evolve.repository.ProductRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CartItemHandler {

    private final CartItemRepository cartItemRepo;
    private final ProductRepository productRepo;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

     public CartItemHandler(CartItemRepository cartItemRepo, ProductRepository productRepo,
                           ReactiveRedisTemplate<String, String> redisTemplate) {
        this.cartItemRepo = cartItemRepo;
        this.productRepo = productRepo;
        this.redisTemplate = redisTemplate;
    }

    public Mono<ServerResponse> list(ServerRequest req) {
        Long cartId = Long.parseLong(req.pathVariable("cartId"));

        Flux<CartItemWithProduct> enrichedItems = cartItemRepo.findAllByCartId(cartId)
            .flatMap(item ->
                productRepo.findById(item.getProductId())
                    .switchIfEmpty(Mono.error(new RuntimeException("Product not found: " + item.getProductId())))
                    .map(product -> {
                        CartItemWithProduct dto = new CartItemWithProduct();
                        dto.setId(item.getId());
                        dto.setCartId(item.getCartId());
                        dto.setQuantity(item.getQuantity());
                        dto.setProduct(product);
                        return dto;
                    })
            );

            return ServerResponse.ok().body(enrichedItems, CartItemWithProduct.class)
            .onErrorResume(e -> {
                e.printStackTrace();
                return ServerResponse.status(500).bodyValue("Internal error: " + e.getMessage());
            });
    }


     public Mono<ServerResponse> add(ServerRequest req) {
        Long cartId = Long.parseLong(req.pathVariable("cartId"));
        return req.bodyToMono(CartItem.class)
                .map(item -> {
                    item.setCartId(cartId);
                    return item;
                })
                .flatMap(cartItemRepo::save)
                .flatMap(saved -> {
                    redisTemplate.convertAndSend("cart-events", "Item added to cart " + cartId).subscribe();
                    return ServerResponse.ok().bodyValue(saved);
                });
    }

    public Mono<ServerResponse> remove(ServerRequest req) {
        Long itemId = Long.parseLong(req.pathVariable("itemId"));
        return cartItemRepo.deleteById(itemId)
                .doOnSuccess(unused -> redisTemplate.convertAndSend("cart-events", "Item removed: " + itemId).subscribe())
                .then(ServerResponse.noContent().build());
    }
}
