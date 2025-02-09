package bg.sofia.uni.fmi.mjt.splitwise.server;

import bg.sofia.uni.fmi.mjt.splitwise.server.command.factory.CommandFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.factory.CommandProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.JsonProcessorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.commandvalidators.CommandValidatorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators.ServiceValidatorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ExpenseServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ExpenseService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.FriendshipService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.NotificationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.UserService;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SplitwiseServer {
    private static final int BUFFER_SIZE = 1024 * 70;
    private static final String HOST = "localhost";
    private static final int PORT_MIN = 1024;
    private final int port;
    //private static final int PORT = 7777;

    private ByteBuffer buffer;
    private Selector selector;
    private boolean isServerWorking;

    private final CommandProcessor commandProcessor;

    public SplitwiseServer(int port) {
        this.port = port;

        UserServiceAPI userService = new UserService(JsonProcessorFactory.getUserJsonProcessor());
        AuthenticationManager authenticationManager = new AuthenticationManager(userService);
        ObligationServiceAPI obligationService =
            new ObligationService(userService, JsonProcessorFactory.getObligationJsonProcessor());
        GroupServiceAPI groupService = new GroupService(JsonProcessorFactory.getGroupJsonProcessor());
        NotificationServiceAPI notificationService =
            new NotificationService(JsonProcessorFactory.getNotificationJsonProcessor());
        FriendshipServiceAPI friendshipService = new FriendshipService(userService);
        ExpenseServiceAPI expenseService =
            new ExpenseService(obligationService, JsonProcessorFactory.getExpenseJsonProcessor());
        ServiceValidatorFactory serviceValidatorFactory =
            ServiceValidatorFactory.getInstance(userService, obligationService, groupService, expenseService,
                notificationService);
        CommandValidatorFactory commandValidatorFactory =
            CommandValidatorFactory.getInstance(authenticationManager, serviceValidatorFactory);

        CommandFactory commandFactory =
            new CommandFactory(authenticationManager, userService, friendshipService, groupService, expenseService,
                obligationService, notificationService, commandValidatorFactory);

        this.commandProcessor = new CommandProcessor(commandFactory, authenticationManager);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            isServerWorking = true;
            while (isServerWorking) {
                try {
                    int readyChannels = selector.select();
                    if (readyChannels == 0) {
                        continue;
                    }
                    handleClientRequests();
                } catch (IOException e) {
                    System.out.println("Error occurred while processing client request " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    private void handleClientRequests() throws IOException {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (key.isReadable()) {
                SocketChannel clientChannel = (SocketChannel) key.channel();
                String clientInput = getClientInput(clientChannel);

                System.out.println(clientInput);
                if (clientInput == null) {
                    continue;
                }
                String response = commandProcessor.processClientInput(clientInput);
                writeClientOutput(clientChannel, response);
            } else if (key.isAcceptable()) {
                accept(selector, key);
            }
            keyIterator.remove();

        }
    }

    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    private void configureServerSocketChannel(ServerSocketChannel serverSocketChannel,
                                              Selector selector) throws IOException {
        serverSocketChannel.bind(new InetSocketAddress(HOST, port));
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        if (!output.endsWith("\n")) {
            output += "\n";
        }
        output += "\n"; // Ensure a newline to signal the end of the message
        buffer.clear();
        buffer.put(output.getBytes(StandardCharsets.UTF_8));
        buffer.flip();

        // Ensure the entire message is written
        while (buffer.hasRemaining()) {
            clientChannel.write(buffer);
        }
        //clientChannel.write(buffer);
        System.out.println("Sent response: " + output);
    }

    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel accept = sockChannel.accept();

        accept.configureBlocking(false);
        accept.register(selector, SelectionKey.OP_READ);
    }

    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();
        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
            clientChannel.close();
            return null;
        }
        buffer.flip();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8).trim();
    }
}
