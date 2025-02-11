package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.*;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotInAnyGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GroupValidatorTest {

    private GroupValidator groupValidator;
    private GroupServiceAPI groupServiceMock;

    @BeforeEach
    void setUp() {
        groupServiceMock = mock(GroupServiceAPI.class);
        groupValidator = new GroupValidator(groupServiceMock);
    }

    @Test
    void testValidateGroupSize_Success() {
        Set<String> members = Set.of("user1", "user2", "user3");

        assertDoesNotThrow(() -> groupValidator.validateGroupSize(members),
            "Should not throw if group has at least 3 members");
    }

    @Test
    void testValidateGroupSize_ThrowsGroupMinimumMemberException() {
        Set<String> members = Set.of("user1");

        assertThrows(GroupMinimumMemberException.class,
            () -> groupValidator.validateGroupSize(members),
            "Should throw GroupMinimumMemberException if group has less than 3 members");
    }

    @Test
    void testValidateGroupName_Success() {
        assertDoesNotThrow(() -> groupValidator.validateGroupName("validGroupName"),
            "Should not throw if group name is valid");
    }

    @Test
    void testValidateGroupName_NullOrBlank_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
            () -> groupValidator.validateGroupName(null),
            "Should throw IllegalArgumentException if group name is null");

        assertThrows(IllegalArgumentException.class,
            () -> groupValidator.validateGroupName(""),
            "Should throw IllegalArgumentException if group name is blank");
    }

    @Test
    void testValidateUserNotInGroup_Success() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertDoesNotThrow(() -> groupValidator.validateUserNotInGroup("group1", "userX"),
            "Should not throw if user is not in the group");
    }

    @Test
    void testValidateUserNotInGroup_ThrowsUserAlreadyPartInTheGroupException() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertThrows(UserAlreadyPartInTheGroupException.class,
            () -> groupValidator.validateUserNotInGroup("group1", "userA"),
            "Should throw UserAlreadyPartInTheGroupException if user is already in the group");
    }

    @Test
    void testValidateUserInsideGroup_Success() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertDoesNotThrow(() -> groupValidator.validateUserInsideGroup("group1", "userA"),
            "Should not throw if user is in the group");
    }

    @Test
    void testValidateUserInsideGroup_ThrowsUserNotInGroupException() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertThrows(UserNotInGroupException.class,
            () -> groupValidator.validateUserInsideGroup("group1", "userX"),
            "Should throw UserNotInGroupException if user is not in the group");
    }

    @Test
    void testIsUserInAnyGroup_Success() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userX", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertDoesNotThrow(() -> groupValidator.isUserInAnyGroup("userX"),
            "Should not throw if user is in at least one group");
    }

    @Test
    void testIsUserInAnyGroup_ThrowsUserNotInAnyGroupException() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertThrows(UserNotInAnyGroupException.class,
            () -> groupValidator.isUserInAnyGroup("userX"),
            "Should throw UserNotInAnyGroupException if user is not in any group");
    }

    @Test
    void testValidateGroupExists_Success() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertDoesNotThrow(() -> groupValidator.validateGroupExists("group1"),
            "Should not throw if group exists");
    }

    @Test
    void testValidateGroupExists_ThrowsGroupDoesntExistsException() {
        Map<String, Set<String>> groups = Map.of();
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertThrows(GroupDoesntExistsException.class,
            () -> groupValidator.validateGroupExists("nonexistentGroup"),
            "Should throw GroupDoesntExistsException if group does not exist");
    }

    @Test
    void testValidateGroupDoesNotExist_Success() {
        Map<String, Set<String>> groups = Map.of();
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertDoesNotThrow(() -> groupValidator.validateGroupDoesNotExist("newGroup"),
            "Should not throw if group does not exist");
    }

    @Test
    void testValidateGroupDoesNotExist_ThrowsGroupAlreadyExistException() {
        Map<String, Set<String>> groups = Map.of("group1", Set.of("userA", "userB"));
        when(groupServiceMock.getGroups()).thenReturn(groups);

        assertThrows(GroupAlreadyExistException.class,
            () -> groupValidator.validateGroupDoesNotExist("group1"),
            "Should throw GroupAlreadyExistException if group already exists");
    }
}
