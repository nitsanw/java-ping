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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PingClient {
    private static final int PAGE_SIZE = 4096;
    private static final int ITERATIONS = 1000000;
    static Histogram hist = new Histogram(100, 1000);

    public static void main(String[] args) throws IOException,
	    InterruptedException {
	String host = args.length > 0 ? args[0] : "localhost";
	int port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
	int messageSize = args.length > 2 ? Integer.parseInt(args[2]) : 32;
	System.out.println("Pinging " + host + ":" + port);
	SocketChannel sc = SocketChannel
		.open(new InetSocketAddress(host, port));
	sc.socket().setTcpNoDelay(true);
	sc.configureBlocking(false);

	ByteBuffer buffy = ByteBuffer.allocateDirect(PAGE_SIZE);

	for(int i=0;i<10;i++){
	    testLoop(messageSize, sc, buffy);
	    hist.clear();
	}	
	if (sc != null) {
	    try {
		sc.close();
	    } catch (IOException ignored) {
	    }
	}
    }

    private static void testLoop(int messageSize, SocketChannel sc,
	    ByteBuffer buffy) throws IOException {
	for (int i = -10000; i < ITERATIONS; i++) {
	    long start = System.nanoTime();
	    ping(sc, buffy, messageSize);
	    long end = System.nanoTime();
	    long time = end - start;
	    observe(i, time);
	}
	report();
    }

    private static void observe(int i, long time) {
	hist.addObservation(time);
	if (i == 0)
	    hist.clear();
    }

    private static void report() {
	System.out.println(hist.toLatencyString(true));
    }

    private static void ping(SocketChannel sc, ByteBuffer bb, int messageSize)
	    throws IOException {
	// send
	bb.position(0);
	bb.limit(messageSize);
	do {
	    sc.write(bb);
	} while (bb.hasRemaining());

	// receive
	bb.clear();
	int bytesRead = 0;
	do {
	    bytesRead += sc.read(bb);
	} while (bytesRead < messageSize);
    }
}
