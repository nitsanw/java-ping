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
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;

public class UdpPingServer {
    private static final int PAGE_SIZE = 4096;

    public static void main(String[] args) throws IOException, InterruptedException {
        String host = args.length > 0 ? args[0] : "localhost";
        int localport = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        int port = args.length > 2 ? Integer.parseInt(args[2]) : 22345;
        System.out.println("Udp server " + host + ":" + port +
                "(binding to localhost:" + localport + ")");
        DatagramChannel sc = DatagramChannel.open();
        sc.configureBlocking(false);
        // bind local
        sc.socket().bind(new InetSocketAddress("localhost", localport));
        // connect remote
        sc.socket().connect(new InetSocketAddress(host, port));
        while (!sc.isConnected())
            ;

        Thread.sleep(10000);
        final ByteBuffer buffy = ByteBuffer.allocateDirect(PAGE_SIZE).order(ByteOrder.nativeOrder());
        try {
            int read = 0;
            while (!Thread.interrupted()) {
                buffy.clear();
                while ((read = sc.read(buffy)) == 0){
                    Helper.yield();
                }
                if (read == -1)
                    return;
                buffy.flip();
                do {
                    sc.write(buffy);
                } while (buffy.hasRemaining());
            }
        } finally {
            if (sc != null) {
                try {
                    sc.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
