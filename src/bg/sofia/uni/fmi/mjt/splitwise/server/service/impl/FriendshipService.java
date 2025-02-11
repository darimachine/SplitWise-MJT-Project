package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.FriendshipServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Collections;
import java.util.Set;

public class FriendshipService implements FriendshipServiceAPI {

    private final UserServiceAPI userService;

    public FriendshipService(UserServiceAPI userService) {
        this.userService = userService;
    }

    @Override
    public void addFriend(String username, String friendUsername) {
        User user = userService.getUser(username);
        User friends = userService.getUser(friendUsername);
        user.addFriend(friendUsername);
        friends.addFriend(username);
        userService.saveAll();
    }

    @Override
    public void removeFriend(String username, String friendUsername) {
        User user = userService.getUser(username);
        User friend = userService.getUser(friendUsername);
        user.removeFriend(friendUsername);
        friend.removeFriend(username);
        userService.saveAll();

    }

    @Override
    public boolean areFriends(String username, String friendUsername) {
        User user = userService.getUser(username);
        return user.getFriends().contains(friendUsername);
    }

    @Override
    public Set<String> getFriendsOf(String username) {
        User user = userService.getUser(username);
        return Collections.unmodifiableSet(user.getFriends());
    }
}
