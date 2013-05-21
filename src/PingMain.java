import java.util.Arrays;

public class PingMain {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
           usage();
           System.exit(1);
        } 

        if ("-server".equalsIgnoreCase(args[0])) {
            try {
                TcpPingServer.main(Arrays.copyOfRange(args, 1, args.length));
                System.exit(0);
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(2);
            }
        }

        if ("-client".equalsIgnoreCase(args[0])) {
            try {
                TcpPingClient.main(Arrays.copyOfRange(args, 1, args.length));
                System.exit(0);
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(3);
            }
        }

        usage();
        System.exit(1);
    }

    public static void usage() {
        System.out.println("java-ping:");
        System.out.println("\t<no-args>\tthis usage");
        System.out.println("\t-server <interface> <port>\t");
        System.out.println("\t    defaults: nic = '0.0.0.0'");
        System.out.println("\t              port = 12345");
        System.out.println("\t-client <host> <port> <message-size>\t");
        System.out.println("\t    defaults: host = 'localhost'");
        System.out.println("\t              port = 12345");
        System.out.println("\t              message-size = 32");
    }
}

