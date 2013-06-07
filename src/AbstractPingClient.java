import java.io.IOException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public abstract class AbstractPingClient {
    private static final int PAGE_SIZE = 4096;
    private static final int ITERATIONS = 100000;
    private static final long[] HISTOGTAM = new long[ITERATIONS];
    int messageSize;
    int port;
    String host;

    public AbstractPingClient(String[] args) throws IOException, InterruptedException {
        initParameters(args);
        initChannel();

        ByteBuffer buffy = ByteBuffer.allocateDirect(PAGE_SIZE).order(ByteOrder.nativeOrder());
        buffy.limit(messageSize);
        System.out.println("Min,50%,90%,99%,99.9%,99.99%,Max");
        for (int i = 0; i < 10; i++) {
            testLoop(buffy);
        }
        cleanup();
    }

    abstract void initChannel() throws IOException, SocketException;

    abstract void cleanup();

    void initParameters(String[] args) {
        host = args.length > 0 ? args[0] : "localhost";
        port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        messageSize = args.length > 2 ? Integer.parseInt(args[2]) : 32;
        System.out.println("Pinging " + host + ":" + port + " messages of size " + messageSize);
    }

    private void testLoop(ByteBuffer buffy) throws IOException {
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            ping(buffy);
            long end = System.nanoTime();
            observe(i, end - start);
        }
        report();
    }

    private static void observe(int i, long time) {
        HISTOGTAM[i]=time;
    }

    private static void report() {
        Arrays.sort(HISTOGTAM);
        System.out.printf("%d,%d,%d,%d,%d,%d,%d\n",HISTOGTAM[0],HISTOGTAM[ITERATIONS/2],HISTOGTAM[(int)(ITERATIONS*0.9)],HISTOGTAM[(int)(ITERATIONS*0.99)],HISTOGTAM[(int)(ITERATIONS*0.999)],HISTOGTAM[(int)(ITERATIONS*0.9999)],HISTOGTAM[ITERATIONS-1] );
    }

    abstract void ping(ByteBuffer bb) throws IOException;

}
