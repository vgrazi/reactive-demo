package com.vgrazi.play;

import org.junit.After;
import org.junit.Test;
import reactive.PriceTick;
import reactive.SomeFeed;
import reactive.SomeListener;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static reactive.Utils.isFastTime;
import static reactive.Utils.isSlowTime;
import static reactive.Utils.sleep;

 
/**
 * Created by victorg on 5/11/2017.
 */
public class ReactorTests {

    @After
    public void after()
    {
        sleep(30_000);
    }
    @Test
    public void test1() {
        Mono.just("Howdy")
                .subscribe(System.out::println);
    }

    @Test
    public void test2() {

        List<String> words = Arrays.asList(
                "the",
                "quick",
                "brown",
                "fox",
                "jumped",
                "over",
                "the",
                "lazy",
                "dog"
        );
        Flux<Integer> range = Flux.range(1, Integer.MAX_VALUE);
        Flux.fromIterable(words)
                .flatMap(word -> Flux.fromArray(word.split("")))
                .sort()
                .distinct()
                .zipWith(range, (word, line) -> line + ":" + word)
                .subscribe(System.out::println);

    }

    @Test
    public void test3() throws InterruptedException {
        Flux<Long> fastTick = Flux.interval(Duration.of(1, ChronoUnit.SECONDS));
        Flux<Long> slowTick = Flux.interval(Duration.of(3, ChronoUnit.SECONDS));

        Flux clock = Flux.merge(
                slowTick.filter(tick -> isSlowTime()),
                fastTick.filter(tick -> isFastTime()));

        Flux<Date> dateFeed = Flux.interval(Duration.of(1, ChronoUnit.SECONDS))
                .map(tick -> new Date());

        clock.withLatestFrom(dateFeed, (tick, date) -> date)
                .subscribe(System.out::println);
    }

    @Test
    public void test() throws InterruptedException {
        SomeFeed<PriceTick> feed = new SomeFeed<>();
        Flux<PriceTick> flux =
                Flux.create(emitter ->
                {
                    SomeListener listener = new SomeListener() {
                        @Override
                        public void priceTick(PriceTick event) {
                            emitter.next(event);
                            if (event.isLast()) {
                                emitter.complete();
                            }
                        }

                        @Override
                        public void error(Throwable e) {
                            emitter.error(e);
                        }
                    };
                            feed.register(listener);
        }, FluxSink.OverflowStrategy.LATEST);
        ConnectableFlux connectable = flux.publish();
        connectable.subscribe(x -> System.out.println("1st " + x));
        Thread.sleep(1000);
        connectable.subscribe(x -> System.out.println("2nd " + x));
        connectable.connect();

    }


}
