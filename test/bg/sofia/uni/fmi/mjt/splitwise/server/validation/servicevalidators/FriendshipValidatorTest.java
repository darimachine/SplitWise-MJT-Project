package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords.ObligationDirection;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FriendshipValidatorTest {

    private FriendshipValidator friendshipValidator;
    private UserServiceAPI userServiceMock;
    private ObligationServiceAPI obligationServiceMock;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        obligationServiceMock = mock(ObligationServiceAPI.class);
        friendshipValidator = new FriendshipValidator(userServiceMock, obligationServiceMock);
    }

    @Test
    void testValidateUserExists_Success() {
        User mockUser = new User("john", "pass123", "John", "Doe");
        when(userServiceMock.getUser("john")).thenReturn(mockUser);

        assertDoesNotThrow(() -> friendshipValidator.validateUserExists("john"),
            "Should not throw if user exists");
    }

    @Test
    void testValidateUserExists_UserNotFound_ThrowsUserNotFoundException() {
        when(userServiceMock.getUser("nonexistent")).thenReturn(null);

        assertThrows(UserNotFoundException.class,
            () -> friendshipValidator.validateUserExists("nonexistent"),
            "Should throw UserNotFoundException if user does not exist");
    }

    @Test
    void testValidateFriendshipDoesNotExist_Success() {
        User user = new User("john", "pass123", "John", "Doe");
        when(userServiceMock.getUser("john")).thenReturn(user);

        assertDoesNotThrow(() -> friendshipValidator.validateFriendshipDoesNotExist("john", "bob"),
            "Should not throw if users are not friends");
    }

    @Test
    void testValidateFriendshipDoesNotExist_AlreadyFriends_ThrowsUserAlreadyFriendsException() {
        User user = new User("john", "pass123", "John", "Doe");
        user.addFriend("bob");
        when(userServiceMock.getUser("john")).thenReturn(user);

        assertThrows(UserAlreadyFriendsException.class,
            () -> friendshipValidator.validateFriendshipDoesNotExist("john", "bob"),
            "Should throw UserAlreadyFriendsException if users are already friends");
    }

    @Test
    void testValidateFriendshipExists_Success() {
        User user = new User("john", "pass123", "John", "Doe");
        user.addFriend("bob");
        when(userServiceMock.getUser("john")).thenReturn(user);

        assertDoesNotThrow(() -> friendshipValidator.validateFriendshipExists("john", "bob"),
            "Should not throw if users are friends");
    }

    @Test
    void testValidateFriendshipExists_NotFriends_ThrowsUserNotFriendsException() {
        User user = new User("john", "pass123", "John", "Doe");
        when(userServiceMock.getUser("john")).thenReturn(user);

        assertThrows(UserNotFriendsException.class,
            () -> friendshipValidator.validateFriendshipExists("john", "bob"),
            "Should throw UserNotFriendsException if users are not friends");
    }

    @Test
    void testValidateRemoveFriendShipIfThereIsObligationsBetweenThem_Success() {
        when(obligationServiceMock.findObligationBetweenUsers("john", "bob")).thenReturn(null);

        assertDoesNotThrow(
            () -> friendshipValidator.validateRemoveFriendShipIfThereIsObligationsBetweenThem("john", "bob"),
            "Should not throw if there are no obligations");
    }

    @Test
    void testValidateRemoveFriendShipIfThereIsObligationsBetweenThem_ObligationExists_ThrowsObligationExistsException() {
        ObligationDirection obligation = new ObligationDirection("john", "bob", 50.0, "BGN");
        when(obligationServiceMock.findObligationBetweenUsers("john", "bob")).thenReturn(obligation);

        assertThrows(ObligationExistsException.class,
            () -> friendshipValidator.validateRemoveFriendShipIfThereIsObligationsBetweenThem("john", "bob"),
            "Should throw ObligationExistsException if there are obligations between users");
    }
}