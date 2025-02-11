package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import java.nio.channels.SocketChannel;

public interface Command {
    String execute(String[] arguments, SocketChannel clientChannel);
}
