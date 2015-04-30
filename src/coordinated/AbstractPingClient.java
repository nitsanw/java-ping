package coordinated;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;
import org.openjdk.jol.info.GraphLayout;

import util.Helper;

public abstract class AbstractPingClient {
    private static final int PAGE_SIZE = 4096;
    private static final int ITERATIONS = 1000000;
    private static final long[] MEASUREMENTS = new long[ITERATIONS];
    private static final Histogram HDR_HISTOGRAM = new Histogram(TimeUnit.MINUTES.toNanos(1), 2);
    // Somewhere, in another class
    protected static HistogramLogWriter histogramLogWriter;

    private static final int LOOP = Integer.getInteger("loop", 30);
    int messageSize;
    int port;
    String host;

    public AbstractPingClient(String[] args) throws IOException {
        initParameters(args);
        initChannel();

        PrintStream log = new PrintStream(new FileOutputStream("HapoelHaifa.hgrm"), false);
        histogramLogWriter = new HistogramLogWriter(log);

        histogramLogWriter.outputLogFormatVersion();
        histogramLogWriter.outputLegend();

        ByteBuffer buffy = ByteBuffer.allocateDirect(PAGE_SIZE).order(ByteOrder.nativeOrder());
        buffy.limit(messageSize);
        System.out.println("Min,50%,90%,99%,99.9%,99.99%,Max");
        for (int i = 0; i < LOOP; i++) {
            testLoop(buffy);
        }
        cleanup();
    }

    abstract void initChannel() throws IOException;

    abstract void cleanup();

    void initParameters(String[] args) {
        host = args.length > 0 ? args[0] : "localhost";
        port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        messageSize = args.length > 2 ? Integer.parseInt(args[2]) : 32;
        System.out.println("Pinging " + host + ":" + port + " messages of size " + messageSize);
        System.out.println(GraphLayout.parseInstance(HDR_HISTOGRAM).toFootprint());
    }

    private void testLoop(ByteBuffer buffy) throws IOException {
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            ping(buffy);
            long end = System.nanoTime();
            observe(i, end - start);
            Helper.waitSome();
        }
        report();
    }

    private static void observe(int i, long time) {
        HDR_HISTOGRAM.recordValue(time);
        MEASUREMENTS[i] = time;
    }

    private static void report() {
        System.out.printf("#%d,%d,%d,%d,%d,%d,%d\n", HDR_HISTOGRAM.getMinValue(),
                HDR_HISTOGRAM.getValueAtPercentile(50), HDR_HISTOGRAM.getValueAtPercentile(90),
                HDR_HISTOGRAM.getValueAtPercentile(99), HDR_HISTOGRAM.getValueAtPercentile(99.9),
                HDR_HISTOGRAM.getValueAtPercentile(99.99), HDR_HISTOGRAM.getMaxValue());
        histogramLogWriter.outputIntervalHistogram(HDR_HISTOGRAM);
        HDR_HISTOGRAM.reset();
        Arrays.sort(MEASUREMENTS);
        System.out.printf("@%d,%d,%d,%d,%d,%d,%d\n", MEASUREMENTS[0], MEASUREMENTS[ITERATIONS / 2],
                MEASUREMENTS[(int) (ITERATIONS / 100 * 90)], MEASUREMENTS[(int) (ITERATIONS / 100 * 99)],
                MEASUREMENTS[(int) (ITERATIONS / 1000 * 999)], MEASUREMENTS[(int) (ITERATIONS / 10000 * 9999)],
                MEASUREMENTS[ITERATIONS - 1]);
    }

    abstract void ping(ByteBuffer bb) throws IOException;

}
