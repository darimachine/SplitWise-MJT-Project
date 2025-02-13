package bg.sofia.uni.fmi.mjt.splitwise.server.security;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationManagerTest {

    private UserServiceAPI userServiceMock;
    private AuthenticationManager authManager;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        authManager = new AuthenticationManager(userServiceMock);
    }

    @Test
    void testLogin_Success() {
        User alex = new User("alex", PasswordHasher.hashPassword("hashedPass123"), "AlexFirst", "AlexLast");

        when(userServiceMock.getUser("alex")).thenReturn(alex);

        SocketChannel channelMock = mock(SocketChannel.class);

        boolean result = authManager.login(channelMock, "alex", "hashedPass123");
        assertTrue(result, "Login should succeed if the hashed passwords match");

        assertTrue(authManager.isAuthenticated(channelMock), "Should be authenticated after success");
        assertEquals(alex, authManager.getAuthenticatedUser(channelMock), "Channel should map to user alex");
    }

    @Test
    void testLogin_FailWrongPassword() {
        User alex = new User("alex", PasswordHasher.hashPassword("hashedPass123"), "AlexFirst", "AlexLast");
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        SocketChannel channelMock = mock(SocketChannel.class);

        boolean result = authManager.login(channelMock, "alex", "wrongPass");
        assertFalse(result, "Login should fail if password doesn't match");

        assertFalse(authManager.isAuthenticated(channelMock));
        assertNull(authManager.getAuthenticatedUser(channelMock));
    }

    @Test
    void testLogin_FailUserNotFound() {
        when(userServiceMock.getUser("nonexistent")).thenReturn(null);

        SocketChannel channelMock = mock(SocketChannel.class);

        boolean result = authManager.login(channelMock, "nonexistent", "pass123");
        assertFalse(result, "Login should fail if user doesn't exist");
        assertFalse(authManager.isAuthenticated(channelMock));
        assertNull(authManager.getAuthenticatedUser(channelMock));
    }

    @Test
    void testIsAuthenticated_ReturnsFalseIfNotLoggedIn() {
        SocketChannel channelMock = mock(SocketChannel.class);
        assertFalse(authManager.isAuthenticated(channelMock), "Should be false if no one logged in on this channel");
    }

    @Test
    void testGetAuthenticatedUser_NoLoginReturnsNull() {
        SocketChannel channelMock = mock(SocketChannel.class);
        User user = authManager.getAuthenticatedUser(channelMock);
        assertNull(user, "No login => null user");
    }

    @Test
    void testLogout_RemovesMapping() {
        User alex = new User("alex", PasswordHasher.hashPassword("hashedPass123"), "AlexFirst", "AlexLast");
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        SocketChannel channelMock = mock(SocketChannel.class);

        boolean loginResult = authManager.login(channelMock, "alex", "hashedPass123");
        assertTrue(loginResult);

        authManager.logout(channelMock);
        assertFalse(authManager.isAuthenticated(channelMock));
        assertNull(authManager.getAuthenticatedUser(channelMock));
    }
}
