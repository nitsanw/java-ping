import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;


public abstract class TcpPingClient extends AbstractPingClient {
    SocketChannel channel;

    public TcpPingClient(String[] args) throws IOException, InterruptedException {
        super(args);
    }
    void initChannel() throws IOException, SocketException {
        channel = SocketChannel.open(new InetSocketAddress(host, port));
        channel.socket().setTcpNoDelay(true);
        channel.configureBlocking(isBlocking());
    }
    protected boolean isBlocking() {
        return false;
    }
    void cleanup() {
        if (channel != null) {
            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
    }

}
