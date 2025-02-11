package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class ShowUserInWhichGroupsParticipateCommandTest {

    private static final String USERNAME = "testUser";
    private static final String[] VALID_ARGS = {"getUserGroups", USERNAME};
    private static final String[] INVALID_ARGS = {}; // No arguments

    private ShowUserInWhichGroupsParticipateCommand command;
    private UserServiceAPI userServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(userServiceMock.getUser(USERNAME)).thenReturn(userMock);

        command = new ShowUserInWhichGroupsParticipateCommand(userServiceMock, validatorMock);
    }

    @Test
    void testExecute_UserParticipatesInGroups_ReturnsGroupList() {
        Set<String> userGroups = Set.of("Group1", "Group2");
        when(userMock.getGroups()).thenReturn(userGroups);

        String expectedMessage = "User 'testUser' participates in the following groups: [Group1, Group2]";
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(userServiceMock).getUser(USERNAME);
        verify(userMock).getGroups();

        assertTrue(result.contains("User 'testUser' participates in the following groups: "),
            "Should return a list of groups the user participates in.");
        assertTrue(result.contains("Group1"), "Should contain the first group.");
        assertTrue(result.contains("Group2"), "Should contain the second group.");
    }

    @Test
    void testExecute_UserHasNoGroups_ReturnsNoGroupsMessage() {
        when(userMock.getGroups()).thenReturn(Set.of()); // User has no groups

        String expectedMessage = "User 'testUser' does not participate in any groups.";
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(userServiceMock).getUser(USERNAME);
        verify(userMock).getGroups();

        assertEquals(expectedMessage, result, "Should return a message stating the user is in no groups.");
    }

    @Test
    void testExecute_ThrowsExceptionWhenValidatorFails() {
        doThrow(new IllegalArgumentException("Invalid arguments provided."))
            .when(validatorMock).validate(INVALID_ARGS, clientChannelMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(INVALID_ARGS, clientChannelMock),
            "Should throw IllegalArgumentException when validation fails.");
    }
}
