package harp.util;

import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Spin {

    static volatile Logger log = Logger.getLogger(Spin.class.getSimpleName());

   /*  public static void main(String[] args) {
        //startTimerHr();
        //
        startTimerBasic(1000);
    } */

    private static int basicInterval = 10;

    public static void startTimerBasic(int intervalParam) {
        try {

            basicInterval = intervalParam;

            for (int h = 0; h < 2; h++) {
                String ops_hr = (h < 10 ? "0" : "") + String.valueOf(h);
                System.out.print(ops_hr + ":");

                for (int m = 0; m < 60; m++) {
                    String ops_mi = (m < 10 ? "0" : "") + String.valueOf(m);
                    System.out.print(ops_mi + ":");

                    for (int s = 0; s != 60; ++s) {

                        String ops = (s < 10 ? "0" : "") + String.valueOf(s);
                        System.out.print(ops);
                        Thread.sleep(basicInterval);
                        clearLine(2);

                    }

                    clearLine(3);
                }

                clearLine(5);
            }

        } catch (Exception e) {
            log.info(e.toString());
        }
    }

    public static void startTimerHr() {
        for (int h = 0; h < 24; h++) {
            String ops = (h < 10 ? "0" : "") + String.valueOf(h);
            System.out.print(ops + ":");
            startTimerMin();
            clearLine(5);
        }
    }

    public static void startTimerMin() {
        for (int m = 0; m < 60; m++) {
            String ops = (m < 10 ? "0" : "") + String.valueOf(m);
            System.out.print(ops + ":");
            startSpinSec();
            clearLine(3);
        }
    }

    public static void startSpinSec() {

        try {
            for (int i = 0; i != 60; ++i) {

                String ops = (i < 10 ? "0" : "") + String.valueOf(i);
                System.out.print(ops);
                Thread.sleep(basicInterval);
                clearLine(2);

            }
        } catch (InterruptedException e) {
            log.info(e.toString());
        }

    }

    public static void clearLine(int len) {
        for (int i = 0; i != len; ++i) {
            System.out.print("\b");
        }
    }

    public static void loader(int pauseInMil) {

        try {

            
            // int codept= "🟩".codePointAt(0);
            //int cc = 0x1F7E9;
            // String load = Character.toString((char) cc);

            Charset utf8 = Charset.forName("UTF-8");
            Charset def = Charset.defaultCharset();

            String charToPrint = "🟩";

            byte[] bytes = charToPrint.getBytes("UTF-8");
            String message = new String(bytes, def.name());

            PrintStream printStream = new PrintStream(System.out, true, utf8.name());
            // printStream.println(message);

            for (int i = 0; i != 110; ++i) {
                if (i % 10 == 0) {
                    // load += specChar;
                    // System.out.print(Character.toString((char)cc)+"\s"+i+"%");
                    // System.out.print(message+"\s"+i+"%");
                    printStream.print(message + "\s" + i + "%");
                }

                Thread.sleep(pauseInMil);

                clearLine(3);

            }
        } catch (Exception e) {
            log.info(e.toString());
        }

    }

}