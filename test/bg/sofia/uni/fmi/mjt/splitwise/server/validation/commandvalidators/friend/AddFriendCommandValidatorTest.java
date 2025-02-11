package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.friend;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.InvalidCommandException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotAuthenticatedException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.FriendshipValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AddFriendCommandValidatorTest {

    private AuthenticationManager authManagerMock;
    private FriendshipValidator friendshipValidatorMock;
    private AddFriendCommandValidator validator;
    private SocketChannel mockChannel;

    @BeforeEach
    void setUp() {
        authManagerMock = mock(AuthenticationManager.class);
        friendshipValidatorMock = mock(FriendshipValidator.class);
        validator = new AddFriendCommandValidator(friendshipValidatorMock, authManagerMock);
        mockChannel = mock(SocketChannel.class);
    }

    @Test
    void testValidate_CorrectArguments_DoesNotThrow() {
        String[] validArgs = {"add-friend", "Steve"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);
        User alex = new User("Alex", "1234", "AlexFirst", "AlexLast");
        when(authManagerMock.getAuthenticatedUser(mockChannel)).thenReturn(alex);

        assertDoesNotThrow(() -> validator.validate(validArgs, mockChannel),
            "Should not throw when adding a valid friend.");
    }

    @Test
    void testValidate_ThrowsIfNotAuthenticated() {
        String[] validArgs = {"add-friend", "Steve"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(false);

        assertThrows(UserNotAuthenticatedException.class,
            () -> validator.validate(validArgs, mockChannel),
            "Should throw UserNotAuthenticatedException when user is not authenticated.");
    }

    @Test
    void testValidate_ThrowsIfInvalidArguments() {
        String[] invalidArgs = {"add-friend"};

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(invalidArgs, mockChannel),
            "Should throw InvalidCommandException when arguments are incorrect.");
    }

    @Test
    void testValidate_ThrowsIfAddingSelf() {
        String[] validArgs = {"add-friend", "Alex"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);
        User alex = new User("Alex", "1234", "AlexFirst", "AlexLast");
        when(authManagerMock.getAuthenticatedUser(mockChannel)).thenReturn(alex);

        assertThrows(InvalidCommandException.class,
            () -> validator.validate(validArgs, mockChannel),
            "Should throw InvalidCommandException when a user tries to add themselves.");
    }

    @Test
    void testValidate_ThrowsIfFriendDoesNotExist() {
        String[] validArgs = {"add-friend", "NonExistentUser"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);
        User alex = new User("Alex", "1234", "AlexFirst", "AlexLast");
        when(authManagerMock.getAuthenticatedUser(mockChannel)).thenReturn(alex);

        doThrow(new UserNotFoundException("User not found")).when(friendshipValidatorMock)
            .validateUserExists("NonExistentUser");

        assertThrows(UserNotFoundException.class,
            () -> validator.validate(validArgs, mockChannel),
            "Should throw UserNotFoundException when adding a nonexistent user.");
    }

    @Test
    void testValidate_ThrowsIfAlreadyFriends() {
        String[] validArgs = {"add-friend", "Steve"};
        when(authManagerMock.isAuthenticated(mockChannel)).thenReturn(true);
        User alex = new User("Alex", "1234", "AlexFirst", "AlexLast");
        when(authManagerMock.getAuthenticatedUser(mockChannel)).thenReturn(alex);

        doThrow(new UserAlreadyFriendsException("Users are already friends")).when(friendshipValidatorMock)
            .validateFriendshipDoesNotExist("Alex", "Steve");

        assertThrows(UserAlreadyFriendsException.class,
            () -> validator.validate(validArgs, mockChannel),
            "Should throw UserAlreadyFriendsException when users are already friends.");
    }
}
