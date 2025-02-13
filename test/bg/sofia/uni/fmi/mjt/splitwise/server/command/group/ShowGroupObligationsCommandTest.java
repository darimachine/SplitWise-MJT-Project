package bg.sofia.uni.fmi.mjt.splitwise.server.command.group;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ShowGroupObligationsCommandTest {

    private static final String GROUP_1 = "Group1";
    private static final String GROUP_2 = "Group2";
    private static final String USERNAME = "testUser";
    private static final String OBLIGATION_RESPONSE = "Group obligations summary";
    private static final String[] VALID_ARGS = {"my-groups"};
    private static final String[] INVALID_ARGS = {}; // No arguments

    private ShowGroupObligationsCommand command;
    private AuthenticationManager authManagerMock;
    private ObligationServiceAPI obligationServiceMock;
    private CommandValidator validatorMock;
    private SocketChannel clientChannelMock;
    private User userMock;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        GroupServiceAPI groupServiceMock = mock(GroupServiceAPI.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        validatorMock = mock(CommandValidator.class);
        clientChannelMock = mock(SocketChannel.class);
        userMock = mock(User.class);

        when(authManagerMock.getAuthenticatedUser(clientChannelMock)).thenReturn(userMock);
        when(userMock.getUsername()).thenReturn(USERNAME);
        when(userMock.getGroups()).thenReturn(Set.of(GROUP_1, GROUP_2));
        when(groupServiceMock.getGroupMembers(GROUP_1)).thenReturn(Set.of("Alice", "Bob"));
        when(groupServiceMock.getGroupMembers(GROUP_2)).thenReturn(Set.of("Charlie", "David"));

        Map<String, Set<String>> expectedGroupMembers = Map.of(
            GROUP_1, Set.of("Alice", "Bob"),
            GROUP_2, Set.of("Charlie", "David")
        );

        when(obligationServiceMock.getMyGroupObligations(USERNAME, expectedGroupMembers)).thenReturn(
            OBLIGATION_RESPONSE);

        command =
            new ShowGroupObligationsCommand(authManagerMock, groupServiceMock, obligationServiceMock, validatorMock);
    }

    @Test
    void testExecute_SuccessfullyReturnsObligations() {
        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(obligationServiceMock).getMyGroupObligations(eq(USERNAME), any());

        assertEquals(OBLIGATION_RESPONSE, result, "Returned obligations summary should match expected.");
    }

    @Test
    void testExecute_UserHasNoGroups_ReturnsEmptySummary() {
        when(userMock.getGroups()).thenReturn(Set.of());
        when(obligationServiceMock.getMyGroupObligations(USERNAME, Map.of())).thenReturn("No group obligations.");

        String result = command.execute(VALID_ARGS, clientChannelMock);

        verify(validatorMock).validate(VALID_ARGS, clientChannelMock);
        verify(authManagerMock).getAuthenticatedUser(clientChannelMock);
        verify(obligationServiceMock).getMyGroupObligations(USERNAME, Map.of());

        assertEquals("No group obligations.", result, "Should return an empty obligations summary.");
    }

    @Test
    void testExecute_ThrowsExceptionWhenValidatorFails() {
        doThrow(new IllegalArgumentException("Invalid arguments provided."))
            .when(validatorMock).validate(INVALID_ARGS, clientChannelMock);

        assertThrows(IllegalArgumentException.class, () -> command.execute(INVALID_ARGS, clientChannelMock),
            "Should throw IllegalArgumentException when validation fails.");
    }
}
