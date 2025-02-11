package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotInAnyGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Set;

public class UserValidator {
    private final UserServiceAPI userService;

    public UserValidator(UserServiceAPI userService) {
        this.userService = userService;
    }

    public void validateUserExist(String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty!");
        }
        User user = userService.getUser(username);
        if (user == null) {
            throw new UserNotFoundException("User with username " + username + " was not found!");
        }
    }

    public void validateUserDoesNotExists(String username) {
        User user = userService.getUser(username);
        if (user != null) {
            throw new UserAlreadyExistException("User with username " + username + " already exists!");
        }
    }

    public void validateIfUserIsInsideAGroup(Set<String> userGroups) {
        if (userGroups == null || userGroups.isEmpty()) {
            throw new UserNotInAnyGroupException("You are not inside any group!");
        }
    }

    public void validateUsersExist(Set<String> members) {
        for (var username : members) {
            validateUserExist(username);
        }
    }
}
