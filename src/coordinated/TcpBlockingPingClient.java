package coordinated;
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

public class TcpBlockingPingClient extends TcpPingClient {

    public TcpBlockingPingClient(String[] args) throws IOException, InterruptedException {
        super(args);
    }
    @Override
    protected boolean isBlocking() {
        return true;
    }
    @Override
    void ping(ByteBuffer bb) throws IOException {
        // send
        channel.write(bb);
        bb.flip();
        // receive
        channel.read(bb);
        bb.flip();
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        new TcpBlockingPingClient(args);
    }
}
