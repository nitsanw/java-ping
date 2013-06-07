import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

public class IpcPingClient {
    private static final int ITERATIONS = 1000000;
    private static final long[] HISTOGTAM = new long[ITERATIONS];

    public static void main(String[] args) throws IOException, InterruptedException {
        int messageSize = args.length > 0 ? Integer.parseInt(args[0]) : 32;
        FileChannel channel = new RandomAccessFile("ping.ipc", "rw").getChannel();
        if (channel.size() < messageSize) {
            System.exit(-1);
        }
        ByteBuffer buffy = channel.map(MapMode.READ_WRITE, 0, 2 * messageSize + 16 + 64 * 3);
        System.out.println("Min,50%,90%,99%,99.9%,99.99%,Max");

        for (int i = 0; i < 10; i++) {
            testLoop(messageSize, buffy);
        }
        channel.close();
    }

    private static void testLoop(int messageSize, ByteBuffer buffy) throws IOException {
        final long inCounterAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 64;
        final long inDataAddress = inCounterAddress + 8;
        final long outCounterAddress = inDataAddress + 64 + messageSize;
        final long outDataAddress = outCounterAddress + 8;
        // set client counter
        for (int i = 0; i < ITERATIONS; i++) {
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
    private static void observe(int i, long time) {
        HISTOGTAM[i]=time;
    }

    private static void report() {
        Arrays.sort(HISTOGTAM);
        System.out.printf("%d,%d,%d,%d,%d,%d,%d\n",HISTOGTAM[0],HISTOGTAM[ITERATIONS/2],HISTOGTAM[(int)(ITERATIONS*0.9)],HISTOGTAM[(int)(ITERATIONS*0.99)],HISTOGTAM[(int)(ITERATIONS*0.999)],HISTOGTAM[(int)(ITERATIONS*0.9999)],HISTOGTAM[ITERATIONS-1] );
    }

}
