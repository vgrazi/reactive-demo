package com.vgrazi.play;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.flowables.ConnectableFlowable;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import reactive.*;
import static reactive.Utils.sleep;

public class ReactiveJavaCanvas {
    @Test
    public void test() {
        Observable.just("Howdy", "World!")
                .subscribe(System.out::println);

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

        Observable.fromIterable(words);
        sleep(60_000);
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
        Observable<String> wordList = Observable.fromIterable(words);
        Observable<Integer> range = Observable.range(1, Integer.MAX_VALUE);
        Observable<String> zipped = wordList
                .flatMap(word->Observable.fromArray(word.split("")))
                .sorted()
                .distinct()
                .zipWith(range, (word, line) -> line + ": " + word);
        zipped.subscribe(System.out::println);


        sleep(60_000);
    }

    private static long start = System.currentTimeMillis();

    public static Boolean isSlowTickTime() {
        return (System.currentTimeMillis() - start) % 12_000 > 6_000;
    }

    @Test
    public void test3() {
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slow = Observable.interval(2, TimeUnit.SECONDS);
        Observable<Long> clock = fast.filter(tick -> !isSlowTickTime()).mergeWith(slow.filter(tick -> isSlowTickTime()));

        Observable<Date> dateFeed = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> new Date());

        clock.withLatestFrom(dateFeed, (tick, date) -> date)
                .subscribe(System.out::println);

        sleep(60_000);
    }

    @Test
    public void test4() {
        SomeFeed feed = new SomeFeed();
        Flowable<Object> flowable = Flowable.create(emitter -> {
            SomeListener listener = new SomeListener() {
                @Override
                public void priceTick(PriceTick event) {
                    if (event.isLast()) {
                        emitter.onComplete();
                    } else {
                        emitter.onNext(event);
                    }
                }

                @Override
                public void error(Throwable throwable) {
                    emitter.onError(throwable);
                }
            };
            feed.register(listener);
        }, BackpressureStrategy.LATEST);

        ConnectableFlowable<Object> publisher = flowable.publish();
        publisher.connect();
        publisher.subscribe(System.out::println);

        sleep(60_000);
    }


}
