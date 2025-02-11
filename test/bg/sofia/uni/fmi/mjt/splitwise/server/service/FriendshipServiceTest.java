package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.FriendshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FriendshipServiceTest {

    private UserServiceAPI userServiceMock;
    private FriendshipService friendshipService;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        friendshipService = new FriendshipService(userServiceMock);
    }

    @Test
    void testAddFriend_UpdatesBothUsersAndSaves() {
        User alice = new User("alice", "1234", "AliceFirst", "AliceLast");
        alice.addFriend("john");

        User bob = new User("bob", "5678", "BobFirst", "BobLast");
        bob.addFriend("john");
        bob.addFriend("mary");

        when(userServiceMock.getUser("alice")).thenReturn(alice);
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        friendshipService.addFriend("alice", "bob");

        // Assert
        // Now, "alice" => friends: {"john","bob"}
        //        "bob"   => friends: {"john","mary","alice"}
        assertTrue(alice.getFriends().contains("bob"), "Alice should now have bob as a friend");
        assertTrue(bob.getFriends().contains("alice"), "Bob should now have alice as a friend");

        verify(userServiceMock).saveAll();
    }

    @Test
    void testRemoveFriend_UpdatesBothUsersAndSaves() {
        User alex = new User("alex", "1234", "AlexFirst", "AlexLast");
        alex.addFriend("peter");
        alex.addFriend("john");
        User peter = new User("peter", "5678", "PeterFirst", "PeterLast");
        peter.addFriend("alex");

        when(userServiceMock.getUser("alex")).thenReturn(alex);
        when(userServiceMock.getUser("peter")).thenReturn(peter);

        friendshipService.removeFriend("alex", "peter");

        // "alex" => friends: {"john"}   (peter removed)
        // "peter" => friends: {}        (alex removed)
        assertFalse(alex.getFriends().contains("peter"), "alex no longer has peter as friend");
        assertFalse(peter.getFriends().contains("alex"), "peter no longer has alex as friend");

        verify(userServiceMock).saveAll();
    }

    @Test
    void testAreFriends_True() {
        User ivan = new User("Ivan", "1234", "IvanFirst", "IvanLast");
        ivan.addFriend("Gosho");

        when(userServiceMock.getUser("Ivan")).thenReturn(ivan);

        boolean result = friendshipService.areFriends("Ivan", "Gosho");
        assertTrue(result, "Ivan and Gosho should be friends");

        verify(userServiceMock, never()).saveAll();
    }

    @Test
    void testAreFriends_False() {
        User gosho = new User("Gosho", "1234", "GoshoFirst", "GoshoLast");
        gosho.addFriend("random");
        when(userServiceMock.getUser("Gosho")).thenReturn(gosho);

        boolean result1 = friendshipService.areFriends("Gosho", "petur");
        assertFalse(result1, "Gosho and petur are not friends");
        verify(userServiceMock, never()).saveAll();
    }

    @Test
    void testGetFriendsOf_ReturnsUnmodifiableSet() {
        User maria = new User("maria", "1234", "MariaFirst", "MariaLast");
        maria.addFriend("alice");
        maria.addFriend("bob");

        when(userServiceMock.getUser("maria")).thenReturn(maria);

        Set<String> result = friendshipService.getFriendsOf("maria");
        assertEquals(2, result.size(), "Should return 2 friends");
        assertTrue(result.contains("alice"));
        assertTrue(result.contains("bob"));

        assertThrows(UnsupportedOperationException.class,
            () -> result.add("charlie"),
            "Returned set should be unmodifiable"
        );

        verify(userServiceMock, never()).saveAll();
    }
}
