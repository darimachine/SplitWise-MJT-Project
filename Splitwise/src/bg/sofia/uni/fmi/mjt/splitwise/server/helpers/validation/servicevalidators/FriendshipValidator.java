package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

public class FriendshipValidator {

    private final UserServiceAPI userService;
    private final ObligationServiceAPI obligationService;

    public FriendshipValidator(UserServiceAPI userService, ObligationServiceAPI obligationService) {
        this.userService = userService;
        this.obligationService = obligationService;
    }

    public void validateAddFriend(String username, String friendUsername) {
        if (username == null || friendUsername == null || username.isBlank() || friendUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty.");
        }
        if (username.equals(friendUsername)) {
            throw new IllegalArgumentException("You cannot add yourself as a friend.");
        }
    }

    public void validateUserExists(String username) {
        if (userService.getUser(username) == null) {
            throw new UserNotFoundException("User does not exist.");
        }
    }

    public void validateFriendshipDoesNotExist(String username, String friendUsername) {
        if (userService.getUser(username).getFriends().contains(friendUsername)) {
            throw new UserAlreadyFriendsException("Users are already friends.");
        }
    }

    public void validateFriendshipExists(String username, String friendUsername) {
        if (!userService.getUser(username).getFriends().contains(friendUsername)) {
            throw new UserNotFriendsException("Users are not friends.");
        }
    }

    public void validateRemoveFriendShipIfThereIsObligationsBetweenThem(String user1, String user2) {
        double amountOwed = obligationService.findObligationBetweenUsers(user1, user2);
        if (amountOwed > 0) {
            throw new ObligationExistsException("You cannot remove friends There are obligations between the users.");
        }
    }
}
