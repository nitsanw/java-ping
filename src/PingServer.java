import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class PingServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        String nic = args.length > 0 ? args[0] : "0.0.0.0";
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 12345;
        System.out.println("Listening on interface : "+nic+":" + port);
        final ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.socket().bind(new InetSocketAddress(nic, port));
        SocketChannel sc2 = null;
        try {
            sc2 = accept(ssc);
            ByteBuffer bb2 = ByteBuffer.allocateDirect(4096);
            while (!Thread.interrupted()) {
                bb2.clear();
                do {
                    if(sc2.read(bb2) == -1)
                        return;
                } while (bb2.position() == 0);
                bb2.flip();
                sc2.write(bb2);
            }
        } catch (ClosedByInterruptException ignored) {
        } catch (ClosedChannelException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(sc2);
        }
        System.in.read();
    }
    private static SocketChannel accept(final ServerSocketChannel ssc)
	    throws IOException, SocketException {
	SocketChannel sc2;
	sc2 = ssc.accept();
	configure(sc2);
	sc2.configureBlocking(false);
	close(ssc);
	return sc2;
    }
    static void close(Closeable sc2) {
        if (sc2 != null) try {
            sc2.close();
        } catch (IOException ignored) {
        }
    }


    static void configure(SocketChannel sc) throws SocketException {
        sc.socket().setTcpNoDelay(true);
    }
}
