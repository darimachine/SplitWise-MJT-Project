package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotInAnyGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserValidatorTest {

    private UserValidator userValidator;
    private UserServiceAPI userServiceMock;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        userValidator = new UserValidator(userServiceMock);
    }

    // -------------------------------------------------------------------------
    // validateUserExist
    // -------------------------------------------------------------------------
    @Test
    void testValidateUserExist_Success() {
        User mockUser = new User("john", "pass123", "John", "Doe");
        when(userServiceMock.getUser("john")).thenReturn(mockUser);

        assertDoesNotThrow(() -> userValidator.validateUserExist("john"),
            "Should not throw if user exists");
    }

    @Test
    void testValidateUserExist_ThrowsIllegalArgumentException_NullUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userValidator.validateUserExist(null),
            "Should throw IllegalArgumentException for null username");
    }

    @Test
    void testValidateUserExist_ThrowsIllegalArgumentException_EmptyUsername() {
        assertThrows(IllegalArgumentException.class,
            () -> userValidator.validateUserExist(""),
            "Should throw IllegalArgumentException for empty username");
    }

    @Test
    void testValidateUserExist_ThrowsUserNotFoundException() {
        when(userServiceMock.getUser("nonexistent")).thenReturn(null);

        assertThrows(UserNotFoundException.class,
            () -> userValidator.validateUserExist("nonexistent"),
            "Should throw UserNotFoundException if user does not exist");
    }

    @Test
    void testValidateUserDoesNotExists_Success() {
        when(userServiceMock.getUser("newUser")).thenReturn(null);

        assertDoesNotThrow(() -> userValidator.validateUserDoesNotExists("newUser"),
            "Should not throw if user does not exist");
    }

    @Test
    void testValidateUserDoesNotExists_ThrowsUserAlreadyExistException() {
        User mockUser = new User("john", "pass123", "John", "Doe");
        when(userServiceMock.getUser("john")).thenReturn(mockUser);

        assertThrows(UserAlreadyExistException.class,
            () -> userValidator.validateUserDoesNotExists("john"),
            "Should throw UserAlreadyExistException if user already exists");
    }

    @Test
    void testValidateIfUserIsInsideAGroup_Success() {
        Set<String> groups = Set.of("group1", "group2");

        assertDoesNotThrow(() -> userValidator.validateIfUserIsInsideAGroup(groups),
            "Should not throw if user is in at least one group");
    }

    @Test
    void testValidateIfUserIsInsideAGroup_ThrowsUserNotInAnyGroupException_EmptySet() {
        assertThrows(UserNotInAnyGroupException.class,
            () -> userValidator.validateIfUserIsInsideAGroup(Set.of()),
            "Should throw UserNotInAnyGroupException if user is in no groups");
    }

    @Test
    void testValidateIfUserIsInsideAGroup_ThrowsUserNotInAnyGroupException_Null() {
        assertThrows(UserNotInAnyGroupException.class,
            () -> userValidator.validateIfUserIsInsideAGroup(null),
            "Should throw UserNotInAnyGroupException if user is in no groups (null case)");
    }

    @Test
    void testValidateUsersExist_Success() {
        when(userServiceMock.getUser("john")).thenReturn(new User("john", "pass123", "John", "Doe"));
        when(userServiceMock.getUser("jane")).thenReturn(new User("jane", "pass123", "Jane", "Doe"));

        assertDoesNotThrow(() -> userValidator.validateUsersExist(Set.of("john", "jane")),
            "Should not throw if all users exist");
    }

    @Test
    void testValidateUsersExist_ThrowsUserNotFoundException() {
        when(userServiceMock.getUser("john")).thenReturn(new User("john", "pass123", "John", "Doe"));
        when(userServiceMock.getUser("jane")).thenReturn(null); // Jane does not exist

        assertThrows(UserNotFoundException.class,
            () -> userValidator.validateUsersExist(Set.of("john", "jane")),
            "Should throw UserNotFoundException if any user does not exist");
    }
}
