package bg.sofia.uni.fmi.mjt.splitwise.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.CLIENT_WELCOME_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.PLEASE_LOGIN_OR_REGISTER_MESSAGE;

public class SplitWiseClient {
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        try (SocketChannel socketChannel = SocketChannel.open();
             BufferedReader reader = new BufferedReader(Channels.newReader(socketChannel, StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(Channels.newWriter(socketChannel, StandardCharsets.UTF_8), true);
             Scanner scanner = new Scanner(System.in)) {
            socketChannel.connect(new InetSocketAddress("localhost", SERVER_PORT));
            System.out.println(CLIENT_WELCOME_MESSAGE);
            System.out.println(PLEASE_LOGIN_OR_REGISTER_MESSAGE);
            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine(); // read a line from the console

                if ("disconnect".equals(message)) {
                    break;
                }
                //System.out.println("Sending message <" + message + "> to the server...");
                writer.println(message);
                writer.flush();
                StringBuilder responseBuilder = new StringBuilder();

                String responseLine;
                while ((responseLine = reader.readLine()) != null && !responseLine.isEmpty()) {
                    responseBuilder.append(responseLine).append(System.lineSeparator());
                }
                System.out.println(responseBuilder);
            }
        } catch (IOException e) {
            throw new RuntimeException("There is a problem with the network communication", e);
        }
    }
}
