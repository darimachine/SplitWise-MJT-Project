package bg.sofia.uni.fmi.mjt.splitwise.server.command.friend;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AreFriendsCommandTest {

    private AreFriendsCommand command;
    private FriendshipServiceAPI friendshipServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;

    private static final String USER_1 = "Alice";
    private static final String USER_2 = "Bob";
    private static final String[] VALID_ARGS = {"are-friends", USER_1, USER_2};

    @BeforeEach
    void setUp() {
        friendshipServiceMock = mock(FriendshipServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);

        command = new AreFriendsCommand(friendshipServiceMock, validatorMock);
    }

    @Test
    void testExecute_UsersAreFriends_ReturnsCorrectMessage() {
        when(friendshipServiceMock.areFriends(USER_1, USER_2)).thenReturn(true);

        String result = command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(friendshipServiceMock).areFriends(USER_1, USER_2);
        assertEquals("User: Alice and User: Bob ARE friends.", result,
            "Should return confirmation message when users are friends.");
    }

    @Test
    void testExecute_UsersAreNotFriends_ReturnsCorrectMessage() {
        when(friendshipServiceMock.areFriends(USER_1, USER_2)).thenReturn(false);
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(friendshipServiceMock).areFriends(USER_1, USER_2);
        assertEquals("User: Alice and User: Bob ARE NOT friends.", result,
            "Should return message when users are NOT friends.");
    }

    @Test
    void testExecute_ValidatorIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
    }

    @Test
    void testExecute_FriendshipServiceIsCalled() {
        command.execute(VALID_ARGS, clientChannelMock);
        verify(friendshipServiceMock).areFriends(USER_1, USER_2);
    }
}
