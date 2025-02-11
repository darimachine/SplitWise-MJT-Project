package bg.sofia.uni.fmi.mjt.splitwise.model;

import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserTest {

    private User user;
    @BeforeEach
    void setUp() {
        user = new User("testUser", "password123", "John", "Doe");
    }
    @Test
    void testConstructor_WithValidArguments_InitializesCorrectly() {
        User user = new User("john_doe", "securePass123", "John", "Doe");

        assertEquals("john_doe", user.getUsername(), "Username should be set correctly.");
        assertEquals("securePass123", user.getPassword(), "Password should be set correctly.");
        assertEquals("John", user.getFirstName(), "First name should be set correctly.");
        assertEquals("Doe", user.getLastName(), "Last name should be set correctly.");
        assertEquals("John Doe", user.getFullName(), "Full name should concatenate first and last name.");
        assertEquals(Currency.BGN, user.getPreferredCurrency(), "Default currency should be BGN.");
        assertNotNull(user.getFriends(), "Friends set should be initialized.");
        assertTrue(user.getFriends().isEmpty(), "Friends set should be empty initially.");
        assertNotNull(user.getGroups(), "Groups set should be initialized.");
        assertTrue(user.getGroups().isEmpty(), "Groups set should be empty initially.");
    }

    @Test
    void testConstructor_WithFriendsAndGroups_InitializesCorrectly() {
        Set<String> friends = new HashSet<>(Set.of("alice", "bob"));
        Set<String> groups = new HashSet<>(Set.of("group1", "group2"));

        User user = new User("john_doe", "securePass123", "John", "Doe", friends, groups);

        assertEquals(friends, user.getFriends(), "Friends should be set correctly.");
        assertEquals(groups, user.getGroups(), "Groups should be set correctly.");
    }

    @Test
    void testAddFriend_NewFriend_IsAddedSuccessfully() {
        User user = new User("john_doe", "securePass123", "John", "Doe");

        user.addFriend("alice");
        assertTrue(user.getFriends().contains("alice"), "Alice should be added as a friend.");
    }

    @Test
    void testAddFriend_ExistingFriend_DoesNotDuplicate() {
        User user = new User("john_doe", "securePass123", "John", "Doe");
        user.addFriend("alice");
        user.addFriend("alice"); // Adding again

        assertEquals(1, user.getFriends().size(), "Friends set should not contain duplicates.");
    }

    @Test
    void testRemoveFriend_ExistingFriend_IsRemovedSuccessfully() {
        User user = new User("john_doe", "securePass123", "John", "Doe");
        user.addFriend("alice");

        user.removeFriend("alice");
        assertFalse(user.getFriends().contains("alice"), "Alice should be removed from friends.");
    }

    @Test
    void testRemoveFriend_NonExistingFriend_DoesNothing() {
        User user = new User("john_doe", "securePass123", "John", "Doe");

        user.removeFriend("bob");
        assertTrue(user.getFriends().isEmpty(), "Removing a non-existing friend should not affect the list.");
    }

    @Test
    void testAddGroup_NewGroup_IsAddedSuccessfully() {
        User user = new User("john_doe", "securePass123", "John", "Doe");

        user.addGroup("work-group");
        assertTrue(user.getGroups().contains("work-group"), "Work-group should be added.");
    }

    @Test
    void testAddGroup_ExistingGroup_DoesNotDuplicate() {
        User user = new User("john_doe", "securePass123", "John", "Doe");
        user.addGroup("family");
        user.addGroup("family"); // Adding again

        assertEquals(1, user.getGroups().size(), "Groups set should not contain duplicates.");
    }

    @Test
    void testGetFullName_ReturnsCorrectFullName() {
        assertEquals("John Doe", user.getFullName(), "Full name should be first name + last name.");
    }

    @Test
    void testSetCurrency_UpdatesPreferredCurrency() {
        User user = new User("john_doe", "securePass123", "John", "Doe");

        user.setCurrency(Currency.USD);
        assertEquals(Currency.USD, user.getPreferredCurrency(), "Preferred currency should be updated to USD.");
    }
    @Test
    void testRemoveGroup_GroupExists_RemovesSuccessfully() {
        user.addGroup("TestGroup");
        assertTrue(user.getGroups().contains("TestGroup"), "User should initially have 'TestGroup'.");

        user.removeGroup("TestGroup");

        assertFalse(user.getGroups().contains("TestGroup"), "User should no longer have 'TestGroup' after removal.");
    }

    @Test
    void testRemoveGroup_GroupDoesNotExist_NoEffect() {
        user.addGroup("ExistingGroup");
        assertTrue(user.getGroups().contains("ExistingGroup"), "User should initially have 'ExistingGroup'.");

        user.removeGroup("NonExistentGroup");

        assertTrue(user.getGroups().contains("ExistingGroup"), "Existing group should not be removed.");
        assertEquals(1, user.getGroups().size(), "The group list size should remain the same.");
    }

    @Test
    void testRemoveGroup_EmptyGroupList_NoEffect() {
        assertTrue(user.getGroups().isEmpty(), "Initially, user should have no groups.");

        user.removeGroup("RandomGroup");

        assertTrue(user.getGroups().isEmpty(), "Removing from an empty set should not change anything.");
    }

    @Test
    void testRemoveGroup_NullGroup_NoEffect() {
        user.addGroup("Group1");
        assertTrue(user.getGroups().contains("Group1"), "User should initially have 'Group1'.");

        user.removeGroup(null);

        assertTrue(user.getGroups().contains("Group1"), "Removing null should not affect existing groups.");
        assertEquals(1, user.getGroups().size(), "The group list size should remain the same.");
    }
}
