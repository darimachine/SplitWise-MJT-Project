package bg.sofia.uni.fmi.mjt.splitwise.server.security;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthenticationManager {

    private final UserServiceAPI userService;
    private final Map<SocketChannel, User> authenticatedUsers = new ConcurrentHashMap<>();

    public AuthenticationManager(UserServiceAPI userService) {
        this.userService = userService;
    }

    /**
     * Attempts to log in the user with the given username and password,
     * associating the session with the provided SocketChannel.
     *
     * @return true if login was successful, false otherwise.
     */
    public boolean login(SocketChannel channel, String username, String pass) {
        User user = userService.getUser(username);
        if (user != null) {
            if (user.getPassword().equals(PasswordHasher.hashPassword(pass))) {
                authenticatedUsers.put(channel, user);
                return true;
            }
        }

        return false;
    }

    public boolean isAuthenticated(SocketChannel channel) {
        return authenticatedUsers.containsKey(channel);
    }

    public User getAuthenticatedUser(SocketChannel channel) {
        return authenticatedUsers.get(channel);
    }

    public void logout(SocketChannel channel) {
        authenticatedUsers.remove(channel);
    }
}
