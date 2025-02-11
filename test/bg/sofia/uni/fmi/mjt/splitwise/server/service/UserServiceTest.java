package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.UserJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    private UserJsonProcessor userJsonHandlerMock;
    private UserService userService;

    private Map<String, User> fakeUsers;

    @BeforeEach
    void setUp() {
        userJsonHandlerMock = mock(UserJsonProcessor.class);

        fakeUsers = new HashMap<>();
        when(userJsonHandlerMock.loadData()).thenReturn(fakeUsers);

        userService = new UserService(userJsonHandlerMock);
    }

    @Test
    void testAddUser_AddsToMapAndSaves() {
        User alice = new User("alice", "1234", "AliceFirst", "AliceLast");
        userService.addUser(alice);

        assertTrue(fakeUsers.containsKey("alice"), "The user map should contain 'alice'");
        assertEquals(alice, fakeUsers.get("alice"), "Should store the same user object");
        verify(userJsonHandlerMock).saveData(fakeUsers);
    }

    @Test
    void testGetUser_NonExistent_ReturnsNull() {
        User result = userService.getUser("bob");
        assertNull(result, "Should return null for unknown username");
    }

    @Test
    void testGetUser_Existing() {
        User charlie = new User("charlie", "1234", "CharlieFirst", "CharlieLast");
        fakeUsers.put("charlie", charlie);

        User result = userService.getUser("charlie");
        assertNotNull(result, "Should not be null for existing user");
        assertEquals(charlie, result, "Should return the same user from map");
    }

    @Test
    void testSaveAll_CallsSaveData() {
        userService.saveAll();
        verify(userJsonHandlerMock).saveData(fakeUsers);
    }

}

