import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class IpcPingClient {
    private static final int ITERATIONS = 1000000;
    static Histogram hist = new Histogram(100000, 10);

    public static void main(String[] args) throws IOException, InterruptedException {
        int messageSize = args.length > 0 ? Integer.parseInt(args[0]) : 32;
        FileChannel channel = new RandomAccessFile("ping.ipc", "rw").getChannel();
        if (channel.size() < messageSize) {
            System.exit(-1);
        }
        ByteBuffer buffy = channel.map(MapMode.READ_WRITE, 0, 2 * messageSize + 16 + 64 * 3);

        for (int i = 0; i < 10; i++) {
            testLoop(messageSize, buffy);
            hist.clear();
        }
        channel.close();
    }

    private static void testLoop(int messageSize, ByteBuffer buffy) throws IOException {
        final long inCounterAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 64;
        final long inDataAddress = inCounterAddress + 8;
        final long outCounterAddress = inDataAddress + 64 + messageSize;
        final long outDataAddress = outCounterAddress + 8;
        // set client counter
        long sendCounterAddress = inDataAddress + messageSize;
        for (long i = -10000; i < ITERATIONS; i++) {
            long start = System.nanoTime();
            // copy message from in to out
            UnsafeAccess.unsafe.copyMemory(inDataAddress, outDataAddress, messageSize);
            UnsafeAccess.unsafe.putOrderedLong(null, inCounterAddress, i);
            // wait for server to set counter
            while (!UnsafeAccess.unsafe.compareAndSwapLong(null, outCounterAddress, i, i))
                ;
            long end = System.nanoTime();
            long time = end - start;
            observe(i, time);
        }
        report();
    }

    private static void observe(long i, long time) {
        hist.addObservation(time);
        if (i == 0)
            hist.clear();
    }

    private static void report() {
        System.out.println(hist.toLatencyString(true));
    }
}
