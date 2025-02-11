package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
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

class ShowGroupMembersCommandTest {

    private static final String GROUP_NAME = "group1";
    private static final String[] VALID_ARGS = {"show-group-members", GROUP_NAME};
    private static final String[] INVALID_ARGS = {"show-group-members"}; // Missing group name

    private GroupServiceAPI groupServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private ShowGroupMembersCommand command;

    @BeforeEach
    void setUp() {
        groupServiceMock = mock(GroupServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        command = new ShowGroupMembersCommand(groupServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfullyReturnsGroupMembers() {
        Set<String> members = Set.of("Alice", "Bob", "Charlie");
        when(groupServiceMock.getGroupMembers(GROUP_NAME)).thenReturn(members);

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(groupServiceMock).getGroupMembers(GROUP_NAME);

        assertTrue(result.contains("Members of group 'group1'"), "Returned members list should contain group name.");
        assertTrue(result.contains("Alice"), "Returned members list should contain Alice.");
        assertTrue(result.contains("Bob"), "Returned members list should contain Bob.");
        assertTrue(result.contains("Charlie"), "Returned members list should contain Charlie.");
    }

    @Test
    void testExecute_EmptyGroupReturnsMessage() {
        when(groupServiceMock.getGroupMembers(GROUP_NAME)).thenReturn(Set.of());

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(groupServiceMock).getGroupMembers(GROUP_NAME);

        String expectedOutput = "👥 Members of group 'group1':\n- ";
        assertEquals(expectedOutput, result, "Should return an empty group message.");
    }

    @Test
    void testExecute_ThrowsExceptionWhenValidatorFails() {
        doThrow(new IllegalArgumentException("Invalid arguments provided."))
            .when(validatorMock).validate(INVALID_ARGS, clientChannelMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(INVALID_ARGS, clientChannelMock),
            "Should throw IllegalArgumentException when validation fails.");
    }
}
