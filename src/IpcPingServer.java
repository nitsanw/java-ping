/*
 * Heavily inspired by http://code.google.com/p/core-java-performance-examples/source/browse/trunk/src/test/java/com/google/code/java/core/socket/PingTest.java
 * And therefore maintaining original licence:
 * -------------------------------------------
 * Copyright (c) 2011.  Peter Lawrey
 *
 * "THE BEER-WARE LICENSE" (Revision 128)
 * As long as you retain this notice you can do whatever you want with this stuff.
 * If we meet some day, and you think this stuff is worth it, you can buy me a beer in return
 * There is no warranty.
 * -------------------------------------------
 * Further mutated by Nitsan Wakart.
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

public class IpcPingServer {
    private static final int ITERATIONS = 1000000;

    public static void main(String[] args) throws IOException, InterruptedException {
        int messageSize = args.length > 0 ? Integer.parseInt(args[0]) : 32;
        FileChannel channel = new RandomAccessFile("ping.ipc", "rw").getChannel();
        if (channel.size() < messageSize) {
            channel.write(ByteBuffer.wrap(new byte[2 * messageSize + 16 + 64 * 3]));
        }
        System.out.println("Ipc server file: ping.ipc messages of size " + messageSize);
        ByteBuffer buffy = channel.map(MapMode.READ_WRITE, 0, 2 * messageSize + 16 + 64 * 3);
        final long inCounterAddress = UnsafeDirectByteBuffer.getAddress(buffy) + 64;
        final long inDataAddress = inCounterAddress + 8;
        final long outCounterAddress = inDataAddress + 64 + messageSize;
        final long outDataAddress = outCounterAddress + 8;

        // wait for server to set counter
        for (int i = 0; i < 10; i++) {
            for (long counter = 0; counter < ITERATIONS; counter++) {
                pong(messageSize, inCounterAddress, inDataAddress, outCounterAddress, outDataAddress, counter);
            }
        }
    }

    private static void pong(int messageSize, long inCounterAddress, long inDataAddress, long outCounterAddress, long outDataAddress, long counter) {
        while (!UnsafeAccess.UNSAFE.compareAndSwapLong(null, inCounterAddress, counter, counter)){
            Helper.yield();
        }
        // copy message from out to in
        UnsafeAccess.UNSAFE.copyMemory(outDataAddress, inDataAddress, messageSize);
        // set client counter
        UnsafeAccess.UNSAFE.putOrderedLong(null, outCounterAddress, counter);
    }
}
