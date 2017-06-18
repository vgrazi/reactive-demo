package com.vgrazi.play;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.flowables.ConnectableFlowable;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import reactive.*;


/**
 * Created by victorg on 5/9/2017.
 */
public class reactive1 {
    @Test
    public void test1() {
        Observable.just("Howdy", "World")
                .subscribe(System.out::println);
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

        Observable<Integer> range = Observable.range(1, Integer.MAX_VALUE);
        Observable.fromIterable(words)
                .flatMap(word -> Observable.fromArray(word.split("")))
                .sorted()
                .distinct()
                .zipWith(range, (word, line) -> line + ":" + word)
                .subscribe(System.out::println);
    }

    @Test
    public void test3() throws InterruptedException {
        Observable fastTick = Observable.interval(1, TimeUnit.SECONDS);
        Observable slowTick = Observable.interval(3, TimeUnit.SECONDS);

        Observable clock = Observable.merge(
                slowTick.filter(tick -> isSlowTickTime()),
                fastTick.filter(tick -> !isSlowTickTime()));

        Observable<Date> dateFeed = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> new Date());

        clock.zipWith(dateFeed, (tick, date) -> date)
                .subscribe(System.out::println);
        Thread.sleep(60_000);

    }

    private static long start = System.currentTimeMillis();

    public static Boolean isSlowTickTime() {
        return (System.currentTimeMillis() - start) % 12_000 > 6_000;
    }

    @Test
    public void test5() throws InterruptedException {
        Observable<Long> slowTick = Observable.interval(2, TimeUnit.SECONDS);
        Observable<Long> fastTick = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> clock = Observable.merge(
                slowTick.filter(time -> isSlowTickTime()),
                fastTick.filter(time -> !isSlowTickTime())
        );

        Observable<Date> dateFeed = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> new Date());

        clock.withLatestFrom(dateFeed, (tick, date) -> date)
                .subscribe(System.out::println);
        Thread.sleep(60_000);
    }

    @Test
    public void test6() throws InterruptedException {
        SomeFeed<PriceTick> someFeed = new SomeFeed<>();
        Flowable<PriceTick> flowable = Flowable.create(emitter ->
        {
            SomeListener listener = new SomeListener() {
                @Override
                public void priceTick(PriceTick event) {
                    emitter.onNext(event);
                }

                @Override
                public void error(Throwable throwable) {
                    emitter.onError(throwable);
                }
            };
            someFeed.register(listener);
        }, BackpressureStrategy.BUFFER);
        ConnectableFlowable<PriceTick> publisher = flowable.publish();
        publisher.connect();
        publisher.subscribe(System.out::println);
        Thread.sleep(60_000);
    }

    @Test
    public void test7() {
        SomeFeed someFeed = new SomeFeed();
        someFeed.register(new SomeListener() {
            @Override
            public void priceTick(PriceTick event) {
                System.out.println(event);
            }

            @Override
            public void error(Throwable throwable) {

            }
        });
    }

    @Test
    public void test8() throws InterruptedException {
        SomeFeed feed = new SomeFeed();
        Flowable<Object> flowable = Flowable.create(emitter ->
                {
                    SomeListener listener = new SomeListener() {
                        @Override
                        public void priceTick(PriceTick event) {
                            emitter.onNext(event);
                        }

                        @Override
                        public void error(Throwable throwable) {
                            emitter.onError(throwable);
                        }
                    };
                    feed.register(listener);
                }, BackpressureStrategy.BUFFER
        );
        ConnectableFlowable<Object> publisher = flowable.publish();
        publisher.connect();
        publisher.subscribe(System.out::println);
        Thread.sleep(60_000);
    }


}
