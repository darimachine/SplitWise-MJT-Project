package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators;

import java.nio.channels.SocketChannel;

public interface CommandValidator {
    void validate(String[] args, SocketChannel clientChannel);
}
