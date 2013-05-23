import java.util.Arrays;

public class PingMain {
    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            usage();
            System.exit(1);
        }
        int exitCode = 0;
        String[] restOfArgs = Arrays.copyOfRange(args, 2, args.length);
        try {
            String role = args[0];
            String type = args[1];

            if ("-server".equalsIgnoreCase(role)) {
                exitCode = 2;
                if ("-spin".equals(type)) {
                    TcpPingServer.main(restOfArgs);
                } else if ("-select".equals(type)) {
                    TcpSelectPingServer.main(restOfArgs);
                } else if ("-selectNow".equals(type)) {
                    TcpSelectNowPingServer.main(restOfArgs);
                } else {
                    // Meh
                    return;
                }
            } else if ("-client".equalsIgnoreCase(role)) {
                exitCode = 3;
                if ("-spin".equals(type)) {
                    TcpPingClient.main(restOfArgs);
                } else if ("-select".equals(type)) {
                    TcpSelectPingClient.main(restOfArgs);
                } else if ("-selectNow".equals(type)) {
                    TcpSelectNowPingClient.main(restOfArgs);
                } else {
                    // Meh
                    return;
                }
            } else {
                exitCode = 1;
                return;
            }
            // Happy ending
            exitCode = 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (exitCode != 0) {
                usage();
            }
            System.exit(exitCode);
        }

    }

    public static void usage() {
        System.out.println("java-ping:");
        System.out.println("\t<no-args>\tthis usage");
        System.out.println("\t-server [-spin|-select|-selectNow] <interface> <port>\t");
        System.out.println("\t    defaults: interface = 0.0.0.0");
        System.out.println("\t              port = 12345");
        System.out.println("\t-client [-spin|-select|-selectNow] <host> <port> <message-size>\t");
        System.out.println("\t    defaults: host = localhost");
        System.out.println("\t              port = 12345");
        System.out.println("\t              message-size = 32");
    }
}
