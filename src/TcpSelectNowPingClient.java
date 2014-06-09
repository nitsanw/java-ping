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
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class TcpSelectNowPingClient extends TcpPingClient {
    Selector select;

    public TcpSelectNowPingClient(String[] args) throws IOException, InterruptedException {
        super(args);
    }
    @Override
    void initChannel() throws IOException {
        super.initChannel();
        select = Selector.open();
        while (!select.isOpen()) {
            Thread.yield();
        }
        channel.register(select, SelectionKey.OP_READ);
    }
    @Override
    void ping(ByteBuffer bb) throws IOException {
        // send
        bb.position(0);
        bb.limit(messageSize);
        do {
            channel.write(bb);
        } while (bb.hasRemaining());
        bb.clear();
        // select
        select();

        // receive
        int bytesRead = 0;
        do {
            bytesRead += channel.read(bb);
        } while (bytesRead < messageSize);    
    }
    void select() throws IOException {
        while (select.selectNow() == 0) {
            Helper.yield();
        }
        select.selectedKeys().clear();
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        new TcpSelectNowPingClient(args);
    }
}
