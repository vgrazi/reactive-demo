package reactive;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PriceTick {
    private final int sequence;
    private final Date date;
    private final String instrument;
    private final double price;
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("hh:mm:ss");


    public PriceTick(int sequence, Date date, String instrument, double price) {
        this.sequence = sequence;
        this.date = date;
        this.instrument = instrument;
        this.price = price;
    }

    public int getSequence() {
        return sequence;
    }

    public Date getDate() {
        return date;
    }

    public String getInstrument() {
        return instrument;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%6d %s %s %s", sequence, DATE_FORMAT.format(new Date()), instrument, price);
    }

    public boolean isLast() {
        return false;
//    return sequence >= 10;
    }

}
