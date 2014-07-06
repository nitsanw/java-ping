package coordinated;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ErrPingClient extends AbstractPingClient {

    public ErrPingClient(String[] args) throws IOException {
        super(args);
    }

    @Override
    void initChannel() throws IOException {
    }

    @Override
    void cleanup() {
    }

    @Override
    void ping(ByteBuffer bb) throws IOException {
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        new ErrPingClient(args);
    }
}
