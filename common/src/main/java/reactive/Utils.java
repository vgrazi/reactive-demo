package reactive;

//import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by vgrazi on 9/2/16.
 */
public class Utils {
    public static void print(Object s) {
        System.out.printf("%s:%s%n", new Date(), s);
    }

    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            System.out.println("Exiting");
        }
    }

    private static long start = System.currentTimeMillis();

    public static Boolean isSlowTime() {
        boolean b = (System.currentTimeMillis() - start) % 12_000 >= 3_000;
        return b;
    }

    public static Boolean isFastTime() {
        return ! isSlowTime();
    }

//    @Test
//    public void test() {
//        while (true) {
//            System.out.println(String.format(new Date() + " is %sslow time ", isSlowTime() ? "" : "NOT "));
//            sleep(1_00);
//        }
//    }

}
