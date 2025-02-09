package bg.sofia.uni.fmi.mjt.splitwise.server.security;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

public class AuthenticationManager {

    private final UserServiceAPI userService;
    private User currentUser;

    public AuthenticationManager(UserServiceAPI userService) {
        this.userService = userService;
        currentUser = null;
    }

    public boolean authenticate(String username, String pass) {
        User user = userService.getUser(username);
        if (user != null) {
            if (user.getPassword().equals(PasswordHasher.hashPassword(pass))) {
                currentUser = user;
                return true;
            }
        }

        return false;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public User getAuthenticatedUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}
