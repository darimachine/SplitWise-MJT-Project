package bg.sofia.uni.fmi.mjt.splitwise.server;

public class SplitwiseServerMain {
    public static void main(String[] args) {
        final int port = 7777;
        SplitwiseServer server = new SplitwiseServer(port);

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop)); // Ensures server stops on shutdown

        server.start();
    }
}
