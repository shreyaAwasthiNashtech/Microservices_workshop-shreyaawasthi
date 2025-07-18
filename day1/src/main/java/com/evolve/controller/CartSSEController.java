package com.evolve.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import javax.annotation.PostConstruct;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequiredArgsConstructor
public class CartSSEController {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final ChannelTopic topic;

    private final CopyOnWriteArrayList<FluxSink<String>> subscribers = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void initSubscriber() {
        redisTemplate.listenToChannel(topic.getTopic())
            .map(message -> message.getMessage())
            .subscribe(msg -> {
                for (FluxSink<String> sink : subscribers) {
                    sink.next(msg);  // Safe as msg is String
                }
            });
    }

    @GetMapping(value = "/cart/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCartUpdates() {
        return Flux.<String>create(subscriber -> {
            subscribers.add(subscriber);
            subscriber.onDispose(() -> subscribers.remove(subscriber));
        });
    }
}
