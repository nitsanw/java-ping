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
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class UdpPingClient extends AbstractPingClient {
    private DatagramChannel sc;
    private int localport;

    public UdpPingClient(String[] args) throws IOException, InterruptedException {
        super(args);
    }

    @Override
    void initChannel() throws IOException {
        sc = DatagramChannel.open();
        sc.configureBlocking(false);
        // bind local
        sc.socket().bind(new InetSocketAddress("localhost", localport));
        // connect remote
        sc.socket().connect(new InetSocketAddress(host, port));
        while (!sc.isConnected())
            ;
    }

    @Override
    void cleanup() {
        if (sc != null) {
            try {
                sc.close();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    void ping(ByteBuffer bb) throws IOException {
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

    @Override
    void initParameters(String[] args) {
        host = args.length > 0 ? args[0] : "localhost";
        port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        localport = args.length > 2 ? Integer.parseInt(args[2]) : 22345;
        messageSize = args.length > 3 ? Integer.parseInt(args[3]) : 32;
        System.out.println("Udp client " + host + ":" + port +
                "(binding to localhost:" + localport + ") messages of size " + messageSize);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new UdpPingClient(args);
    }
}
