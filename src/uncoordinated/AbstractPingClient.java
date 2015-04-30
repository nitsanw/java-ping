package uncoordinated;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import util.Helper;


public abstract class AbstractPingClient {
    private static final int PAGE_SIZE = 4096;
    private static final int ITERATIONS = Integer.getInteger("iterations", 100000);
    private static final long WAIT_BETWEEN_SND = TimeUnit.MICROSECONDS.toNanos(Integer.getInteger("wait.us", 20));
    private static final long[] HISTOGRAM = new long[ITERATIONS];
 
    private static final int LOOP = Integer.getInteger("loop",30);
    int messageSize;
    int port;
    String host;
    final CyclicBarrier iterationBarrier = new CyclicBarrier(2);
    public AbstractPingClient(String[] args) throws Exception {
        initParameters(args);
        initChannel();

        new Thread("Receive"){
        	public void run() {
        		System.out.println("Min,50%,90%,99%,99.9%,99.99%,Max");
        		ByteBuffer buffy = ByteBuffer.allocateDirect(PAGE_SIZE).order(ByteOrder.nativeOrder());
                buffy.limit(messageSize);
                for (int i = 0; i < LOOP; i++) {
                    try {
						rcvLoop(buffy);
						iterationBarrier.await();
						iterationBarrier.reset();
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(-1);
					}
                }		
        		cleanup();
        	}
        }.start();
        ByteBuffer buffy = ByteBuffer.allocateDirect(PAGE_SIZE).order(ByteOrder.nativeOrder());
        buffy.limit(messageSize);
        
        for (int i = 0; i < LOOP; i++) {
            sndLoop(buffy);
        }
        
    }

    abstract void initChannel() throws IOException;

    abstract void cleanup();

    void initParameters(String[] args) {
        host = args.length > 0 ? args[0] : "localhost";
        port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        messageSize = args.length > 2 ? Integer.parseInt(args[2]) : 32;
        System.out.println("Pinging " + host + ":" + port + " messages of size " + messageSize);
    }

    private void sndLoop(ByteBuffer buffy) throws IOException, InterruptedException, BrokenBarrierException {
        for (int i = 0; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            buffy.putLong(0, start);
            pingSnd(buffy);
            // wait 100us
            Helper.waitSome(WAIT_BETWEEN_SND);
        }
        iterationBarrier.await();
        
    }
    private void rcvLoop(ByteBuffer buffy) throws IOException {
        for (int i = 0; i < ITERATIONS;) {
            i = pingRcv(i, buffy);
        }
        report();
    }

    protected static void observe(int i, long time) {
        HISTOGRAM[i]=time;
    }

    private static void report() {
        Arrays.sort(HISTOGRAM);
        System.out.printf("%d,%d,%d,%d,%d,%d,%d\n", HISTOGRAM[0], HISTOGRAM[ITERATIONS/2], HISTOGRAM[(int)(ITERATIONS*0.9)], HISTOGRAM[(int)(ITERATIONS*0.99)], HISTOGRAM[(int)(ITERATIONS*0.999)], HISTOGRAM[(int)(ITERATIONS*0.9999)], HISTOGRAM[ITERATIONS-1] );
    }

    abstract void pingSnd(ByteBuffer bb) throws IOException;
    abstract int pingRcv(int i, ByteBuffer bb) throws IOException;

}
