package uncoordinated;
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
    public TcpSpinPingClient(String[] args) throws Exception {
        super(args);
    }

	@Override
	void pingSnd(ByteBuffer bb) throws IOException {
		bb.position(0);
        bb.limit(messageSize);
        do {
            channel.write(bb);
        } while (bb.hasRemaining());
	}

	@Override
	int pingRcv(int i, ByteBuffer buffy) throws IOException {
        int bytesRead = buffy.position();
        do {
            bytesRead += channel.read(buffy);
        } while (bytesRead < messageSize);
        
        long end = System.nanoTime();
        int leftOver = bytesRead % messageSize;
        if(leftOver > 0) {
        	buffy.limit(bytesRead);
        	bytesRead = bytesRead - leftOver;
        	buffy.position(bytesRead);
		}        
        for(int offset=0; offset < bytesRead;offset += messageSize) {
        	long start = buffy.getLong(offset);
        	observe(i++, end - start);
        }
        if(leftOver > 0) {
        	buffy.compact();
        }
        else {
        	buffy.clear();
        }
        return i;
	}

	public static void main(String[] args) throws Exception {
        new TcpSpinPingClient(args);
    }
}
