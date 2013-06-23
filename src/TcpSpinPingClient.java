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
import java.nio.ByteBuffer;

public class TcpSpinPingClient extends TcpPingClient {
    public TcpSpinPingClient(String[] args) throws IOException, InterruptedException {
        super(args);
    }

    void ping(ByteBuffer bb) throws IOException {
        // send
        bb.position(0);
        bb.limit(messageSize);
        do {
            channel.write(bb);
        } while (bb.hasRemaining());

        // receive
        bb.clear();
        int bytesRead = 0;
        do {
            bytesRead += channel.read(bb);
        } while (bytesRead < messageSize);
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        new TcpSpinPingClient(args);
    }
}
