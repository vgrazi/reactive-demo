package reactive;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SomeFeed<T> {
    private final boolean barriered;
    private AtomicInteger threadcounter = new AtomicInteger(1);
    private ExecutorService service = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r);
        thread.setName("Thread " + threadcounter.getAndIncrement());
        return thread;
    });

    private transient boolean running = true;
    private List<SomeListener> listeners = new LinkedList<>();

    private int threadCount;
    private CyclicBarrier barrier;
    private final Random RANDOM = new Random(0);

    private static final Random RANDOM_PRICE = new Random(0);
    private static final String[] instruments = {"IBM", "NMR", "BAC", "AAPL", "MSFT"};

    public SomeFeed() {
        this(instruments.length);
    }

    private SomeFeed(int threadCount) {
        this(threadCount, false);
    }


    private SomeFeed(int threadCount, boolean barriered) {
        this.threadCount = threadCount;
        this.barriered = barriered;
        if (barriered) {
            barrier = new CyclicBarrier(threadCount, System.out::println);
        }
        launchPublishers();
        SomeListener tickOutputter = new SomeListener() {
            @Override
            public void priceTick(PriceTick event) {
                System.out.println(".");
            }

            @Override
            public void error(Throwable throwable) {

            }
        };
//        register(tickOutputter);
    }

    AtomicInteger sequence = new AtomicInteger(1);


    private void launchEventThread(String instrument, double startingPrice) {
        service.execute(() ->
        {
            final Object MUTEX = new Object();
            SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss.SSS");
            double price = startingPrice;
            while (running) {
                try {
                    if (barriered) {
                        barrier.await();
                    }
                    price += RANDOM_PRICE.nextGaussian();

                    double finalPrice = price;
                    listeners.forEach(subscriber -> {
                        PriceTick tick = new PriceTick(sequence.getAndIncrement(), new Date(), instrument, finalPrice);
                        String message = String.format("%s %s %s", format.format(new Date()), instrument, finalPrice);
//            Utils.print("Notifying " + message);
                        subscriber.priceTick(tick);
                    });
                    synchronized (MUTEX) {
                        MUTEX.wait(RANDOM.nextInt(200) + 800);
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    double[] prices = {160, 5, 15, 108, 57};

    void launchPublishers() {
        Utils.print("Launching publishers");
        for (int i = 0; i < threadCount; i++) {
            launchEventThread(instruments[i % instruments.length], prices[i % prices.length]);
        }
    }

    public void register(SomeListener listener) {
        Utils.print("Registering subscriber " + listener);
        listeners.add(listener);
    }

    public void terminate() {
        running = false;
    }

    public static void main(String[] args) {
        SomeFeed<Object> feed = new SomeFeed<>();
        feed.launchPublishers();
        feed.register(new SomeListener() {
            @Override
            public void priceTick(PriceTick event) {
                System.out.println(event);
            }

            @Override
            public void error(Throwable throwable) {

            }
        });
    }

}
