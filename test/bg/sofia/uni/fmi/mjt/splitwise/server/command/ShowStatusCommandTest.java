package bg.sofia.uni.fmi.mjt.splitwise.server.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowStatusCommandTest {

    private static final String FRIENDS_MESSAGE = "Friends: You owe John $10.\n";
    private static final String GROUPS_MESSAGE = "Groups: You owe TeamAlpha $20.\n";
    private static final String EXPECTED_RESULT = FRIENDS_MESSAGE + GROUPS_MESSAGE;
    private static final String[] VALID_ARGS = {"show-status"};

    private Command showFriendsCommandMock;
    private Command showGroupCommandMock;
    private SocketChannel clientChannelMock;
    private ShowStatusCommand command;

    @BeforeEach
    void setUp() {
        showFriendsCommandMock = mock(Command.class);
        showGroupCommandMock = mock(Command.class);
        clientChannelMock = mock(SocketChannel.class);

        command = new ShowStatusCommand(showFriendsCommandMock, showGroupCommandMock);
    }

    @Test
    void testExecute_ValidArguments_ReturnsConcatenatedResult() {
        when(showFriendsCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn(FRIENDS_MESSAGE);
        when(showGroupCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn(GROUPS_MESSAGE);

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(showFriendsCommandMock).execute(VALID_ARGS, clientChannelMock);
        verify(showGroupCommandMock).execute(VALID_ARGS, clientChannelMock);

        assertEquals(EXPECTED_RESULT, result, "Should return concatenated results of both commands.");
    }

    @Test
    void testExecute_EmptyFriendStatus_ReturnsOnlyGroupStatus() {
        when(showFriendsCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn("");
        when(showGroupCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn(GROUPS_MESSAGE);

        String result = command.execute(VALID_ARGS, clientChannelMock);

        assertEquals(GROUPS_MESSAGE, result, "Should return only group status if friend status is empty.");
    }

    @Test
    void testExecute_EmptyGroupStatus_ReturnsOnlyFriendStatus() {
        when(showFriendsCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn(FRIENDS_MESSAGE);
        when(showGroupCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn("");

        String result = command.execute(VALID_ARGS, clientChannelMock);

        assertEquals(FRIENDS_MESSAGE, result, "Should return only friend status if group status is empty.");
    }

    @Test
    void testExecute_BothStatusesEmpty_ReturnsEmptyString() {
        when(showFriendsCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn("");
        when(showGroupCommandMock.execute(VALID_ARGS, clientChannelMock)).thenReturn("");

        String result = command.execute(VALID_ARGS, clientChannelMock);

        assertEquals("", result, "Should return an empty string if both statuses are empty.");
    }
}
