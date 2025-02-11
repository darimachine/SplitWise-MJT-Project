package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import java.nio.channels.SocketChannel;

public class ShowStatusCommand implements Command {
    private final Command showFriendsCommand;
    private final Command showGroupCommand;

    public ShowStatusCommand(Command showFriendsCommand, Command showGroupCommand) {
        this.showFriendsCommand = showFriendsCommand;
        this.showGroupCommand = showGroupCommand;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        String myFriendMessage = showFriendsCommand.execute(arguments, clientChannel);
        String myGroupMessage = showGroupCommand.execute(arguments, clientChannel);
        return myFriendMessage + myGroupMessage;
    }
}
