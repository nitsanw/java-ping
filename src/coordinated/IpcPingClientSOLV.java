package coordinated;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Arrays;

import util.Helper;
import util.UnsafeAccess;
import util.UnsafeDirectByteBuffer;

public class IpcPingClientSOLV {
    private static final int ITERATIONS = 1000000;
    private static final long[] HISTOGRAM = new long[ITERATIONS];

    public static void main(String[] args) throws IOException, InterruptedException {
        int messageSize = args.length > 0 ? Integer.parseInt(args[0]) : 32;
        @SuppressWarnings("resource")
		FileChannel channel = new RandomAccessFile("ping.ipc", "rw").getChannel();
        if (channel.size() < messageSize) {
            System.exit(-1);
        }
        System.out.println("Ipc client file: ping.ipc messages of size " + messageSize);
        ByteBuffer buffy = channel.map(MapMode.READ_WRITE, 0, 2 * messageSize + 16 + 64 * 3);
        System.out.println("Min,50%,90%,99%,99.9%,99.99%,Max");

        for (int i = 0; i < 100; i++) {
            testLoop(messageSize, buffy);
        }
        channel.close();
    }

    private static void testLoop(int messageSize, ByteBuffer buffy) {
        final long inCounterAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 64;
        final long inDataAddress = inCounterAddress + 8;
        final long outCounterAddress = inDataAddress + 64 + messageSize;
        final long outDataAddress = outCounterAddress + 8;
        // set client counter
        for (int i = 0; i < ITERATIONS; i++) {
            ping(messageSize, inCounterAddress, inDataAddress, outCounterAddress, outDataAddress, i);
        }
        report();
    }

    private static void ping(int messageSize, long inCounterAddress, long inDataAddress, long outCounterAddress, long outDataAddress, int counter) {
        long start = System.nanoTime();
        // copy message from in to out
        UnsafeAccess.UNSAFE.copyMemory(inDataAddress, outDataAddress, messageSize);
        UnsafeAccess.UNSAFE.putOrderedLong(null, inCounterAddress, counter);
        // wait for server to set counter
        while (counter != UnsafeAccess.UNSAFE.getLongVolatile(null, outCounterAddress)) {
            Helper.yield();
        }
        long end = System.nanoTime();
        long time = end - start;
        observe(counter, time);
    }

    private static void observe(int i, long time) {
        HISTOGRAM[i]=time;
    }

    private static void report() {
        Arrays.sort(HISTOGRAM);
        System.out.printf("%d,%d,%d,%d,%d,%d,%d\n", HISTOGRAM[0], HISTOGRAM[ITERATIONS/2], HISTOGRAM[(int)(ITERATIONS*0.9)], HISTOGRAM[(int)(ITERATIONS*0.99)], HISTOGRAM[(int)(ITERATIONS*0.999)], HISTOGRAM[(int)(ITERATIONS*0.9999)], HISTOGRAM[ITERATIONS-1] );
    }

}
