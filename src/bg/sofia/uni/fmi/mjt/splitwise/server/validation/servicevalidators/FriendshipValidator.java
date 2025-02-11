package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.obligation.ObligationExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserAlreadyFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotFriendsException;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords.ObligationDirection;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

public class FriendshipValidator {

    private final UserServiceAPI userService;
    private final ObligationServiceAPI obligationService;

    public FriendshipValidator(UserServiceAPI userService, ObligationServiceAPI obligationService) {
        this.userService = userService;
        this.obligationService = obligationService;
    }

    public void validateUserExists(String username) {
        if (userService.getUser(username) == null) {
            throw new UserNotFoundException("User with that username: " + username + " does not exist.");
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
        ObligationDirection obligation = obligationService.findObligationBetweenUsers(user1, user2);
        if (obligation != null) {
            throw new ObligationExistsException("You cannot remove friends There are obligations between the users.");
        }
    }
}
