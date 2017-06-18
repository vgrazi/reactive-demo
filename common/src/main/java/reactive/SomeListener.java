package reactive;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by victorg on 7/27/2016.
 */
public abstract class SomeListener {
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private final int ID;
    public SomeListener()
    {
        ID = COUNTER.getAndIncrement();
    }

    public abstract void priceTick(PriceTick event);
    public abstract void error(Throwable throwable);

    @Override
    public String toString() {
        return String.format("Listener ID:%d:%s", ID, super.toString());
    }
}
