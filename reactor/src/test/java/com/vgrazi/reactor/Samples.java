package com.vgrazi.reactor;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactive.PriceTick;
import reactive.SomeFeed;
import reactive.SomeListener;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static reactive.Utils.isFastTime;
import static reactive.Utils.isSlowTime;
import static reactive.Utils.sleep;

public class Samples {
    @AfterEach
    public void after() {
        sleep(10_000);
    }

    @Test
    public void test1() {
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println();
            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        Flux.just("Howdy", "Word")
                .subscribe(subscriber);
    }

    @Test
    public void test2() {
        List<String> words = Arrays.asList(
                "the",
                "quick",
                "brown",
                "fox",
                "jumps",
                "over",
                "the",
                "lazy",
                "dog"
        );
        Flux<Integer> lines = Flux.range(1, Integer.MAX_VALUE);
        Flux<String> wordsFlux = Flux.fromIterable(words);
        wordsFlux
                .flatMap(word -> Flux.fromArray(word.split("")))
                .distinct()
                .sort()
                .zipWith(lines, (word, line) -> line + " " + word)
                .subscribe(System.out::println);
    }

    @Test
    public void test3() {
        Flux<Long> fast = Flux.interval(Duration.ofSeconds(1));
        Flux<Long> slow = Flux.interval(Duration.ofSeconds(3));

        Flux<Long> clock = Flux.merge(
                fast.filter(t -> isFastTime()),
                slow.filter(t -> isSlowTime())
        );

        Flux<LocalDateTime> dateEmitter = Flux.interval(Duration.ofSeconds(1))
                .map(t -> LocalDateTime.now());

        Flux<LocalDateTime> localDateTimeFlux = clock.withLatestFrom(dateEmitter, (tick, date) -> date);

        localDateTimeFlux.subscribe(t -> System.out.println(t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"))));
    }

    @Test
    public void test4() {
        SomeFeed<PriceTick> feed = new SomeFeed<>();
        Flux<Object> priceFlux = Flux.create(emitter ->
        {
            SomeListener l = new SomeListener() {
                @Override
                public void priceTick(PriceTick event) {
                    emitter.next(event);
                }

                @Override
                public void error(Throwable throwable) {
                    emitter.error(throwable);
                }

            };
            feed.register(l);
        }, FluxSink.OverflowStrategy.LATEST);
        ConnectableFlux<Object> connectableFlux = priceFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(System.out::println);
    }


}
