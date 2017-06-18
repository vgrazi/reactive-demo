package com.vgrazi.play;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import org.junit.Test;
import reactive.PriceTick;
import reactive.SomeFeed;
import reactive.SomeListener;
import reactive.Utils;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by victorg on 4/30/2017.
 */
public class reactive {
    @Test
    public void testReactive() {
        Observable.just("Hello", "World")
                .subscribe(System.out::println);
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

        Observable<String> wordsObs = Observable.fromIterable(words);
        wordsObs.subscribe(System.out::println);

        Observable<Integer> range = Observable.range(1, Integer.MAX_VALUE);
//        range.subscribe(System.out::println);

        wordsObs.zipWith(range, (word, row) -> String.format("%2d. %s", row, word))
                .subscribe(System.out::println);

        wordsObs.flatMap(word -> Observable.fromArray(word.split("")))
                .sorted()
                .distinct()
                .zipWith(range, (word, row) -> String.format("%2d. %s", row, word))
                .subscribe(System.out::println);
    }

    @Test
    public void test() {
        Observable.just("Hello", "World")
                .subscribe(System.out::println);
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

        Observable.fromIterable(words)
                .subscribe(System.out::println);

        Observable<Integer> range = Observable.range(1, Integer.MAX_VALUE);
//        range.subscribe(System.out::println);

        Observable.fromIterable(words)
                .flatMap(word -> Observable.fromArray(word.split("")))
                .sorted()
                .distinct()
                .zipWith(range, (word, row) -> String.format("%2d. %s", row, word))
                .subscribe(System.out::println);
    }

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
                .zipWith(range, (word, row) -> String.format("%2d. %s", row, word))
                .subscribe(System.out::println);


    }

    @Test
    public void test3() {
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
        Observable.fromIterable(words)
                .flatMap(word -> Observable.fromArray(word.split("")))
                .sorted()
                .distinct()
                .zipWith(Observable.range(1, Integer.MAX_VALUE), (word, row) -> String.format("%2d. %s", row, word))
                .subscribe(System.out::println);
    }

    private static long start = System.currentTimeMillis();

    public static Boolean isSlowTickTime() {
        return (System.currentTimeMillis() - start) % 20_000 >= 10_000;
    }

//    @Test
//    public void test4() throws InterruptedException {
//        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
//        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);
//
//
////        slow.subscribe(x -> System.out.println(new Date()));
//        Observable range = Observable.range(1, Integer.MAX_VALUE);
//        Observable<Long> observable = Observable.merge(
//                slow.filter(x -> isSlowTickTime()),
//                fast.filter(x -> !isSlowTickTime())
//        )
//                .debounce(1, TimeUnit.MILLISECONDS)
//                .zipWith(range, (x, row) -> String.format("%2d. %s", row, new Date()));
//        observable
//                .subscribe(System.out::println);
////                .subscribe(x1 -> System.out.println(new Date()));
//
//        Thread.sleep(60_000);
//    }

    @Test
    public void test5() throws InterruptedException {
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);

        Observable<Long> clock =
                slow.filter(x -> isSlowTickTime())
                        .mergeWith(fast.filter(x -> !isSlowTickTime()));
        Observable<Date> feed = Observable.interval(1, TimeUnit.SECONDS)
                .map(x -> new Date());

        clock.zipWith(feed, (tick, event) -> String.format("%s", event))
                .subscribe(System.out::println);

        Thread.sleep(60_000);
    }

    @Test
    public void test6() throws InterruptedException {
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);

        Observable<Long> clock = slow.filter(x -> isSlowTickTime())
                .mergeWith(fast.filter(x -> !isSlowTickTime()));

        Observable<Date> feed = Observable.interval(1, TimeUnit.SECONDS)
                .map(x -> new Date());

        clock.zipWith(feed, (tick, event) -> event.toString())
                .subscribe(System.out::println);

        Thread.sleep(60_000);
    }

    @Test
    public void test7() {
        Observable.just("Howdy", "World")
                .subscribe(System.out::println);
    }

    @Test
    public void test8() {
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
        Observable<String> wordsObs = Observable.fromIterable(words)
                .flatMap(word -> Observable.fromArray(word.split("")));
        wordsObs.subscribe(System.out::println);

        Observable<Integer> range = Observable.range(1, Integer.MAX_VALUE);

        wordsObs
                .distinct()
                .sorted()
                .zipWith(range, (word, row) -> row + ":" + word)

                .subscribe(System.out::println);
    }

    @Test
    public void test9() throws InterruptedException {
        Observable<Long> fast = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slow = Observable.interval(3, TimeUnit.SECONDS);
        Observable clock = fast.filter(tick -> !isSlowTickTime()).mergeWith(slow.filter(tick -> isSlowTickTime()));
//                clock.subscribe(System.out::println);
        Observable<Date> eventStream = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick -> new Date());
//        eventStream.subscribe(System.out::println);

        eventStream.zipWith(clock, (event, tick) -> event)
                .subscribe(System.out::println);
        Thread.sleep(60_000);
    }

    @Test
    public void test10() throws InterruptedException {
        SomeFeed<PriceTick> feed = new SomeFeed<>();

        Flowable<PriceTick> flowable = Flowable.create(emitter -> {
            SomeListener listener = new SomeListener() {
                @Override
                public void priceTick(PriceTick event) {
                    emitter.onNext(event);
                    if (event.isLast()) {
                        emitter.onComplete();
                    }
                }

                @Override
                public void error(Throwable e) {
                    emitter.onError(e);
                }
            };
            feed.register(listener);
        }, BackpressureStrategy.LATEST);
        flowable.subscribe(System.out::println);
        Thread.sleep(60_000);
    }

    @Test
    public void test11() {
        Observable.just("Howdy", "World")
                .subscribe(System.out::println);
    }

    @Test
    public void test12() {
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
                .zipWith(range, (word, row)->row + ":" + word)
                .subscribe(System.out::println);
    }

    @Test
    public void test13() throws InterruptedException {


        Observable<Long> fastTick = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> slowTick = Observable.interval(3, TimeUnit.SECONDS);

        Observable<Date> feed = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick->new Date());

        slowTick.filter(tick->isSlowTickTime())
                .mergeWith(fastTick.filter(tick->!isSlowTickTime()))
                .zipWith(feed, (tick, tick1)->tick1)
                .subscribe(System.out::println);


        Thread.sleep(60_000);
    }

    @Test
    public void test14() throws InterruptedException {
        Observable slowTick = Observable.interval(3, TimeUnit.SECONDS);
        Observable fastTick = Observable.interval(1, TimeUnit.SECONDS);

        Observable feed = Observable.interval(1, TimeUnit.SECONDS).map(tick->new Date());
        slowTick.filter(tick->isSlowTickTime())
                .mergeWith(fastTick.filter(tick->!isSlowTickTime()))
                .zipWith(feed, (tick, feed1) -> feed1)
                .subscribe(System.out::println);

        Thread.sleep(60_000);
    }

    @Test
    public void test15() throws InterruptedException {
        Observable fastTick = Observable.interval(1, TimeUnit.SECONDS);
        Observable slowTick = Observable.interval(3, TimeUnit.SECONDS);

        Observable dateFeed = Observable.interval(1, TimeUnit.SECONDS)
                .map(tick-> new Date());
        slowTick.filter(tick->isSlowTickTime())
                .mergeWith(fastTick.filter((tick)->!isSlowTickTime()))
                .zipWith(dateFeed, (tick, feed)->feed)
                .subscribe(System.out::println);
        Thread.sleep(60_000);
    }


}
