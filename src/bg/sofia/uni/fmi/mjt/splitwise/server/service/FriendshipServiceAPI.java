package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import java.util.Set;

public interface FriendshipServiceAPI {

    void addFriend(String username, String friendUsername);

    void removeFriend(String username, String friendUsername);

    boolean areFriends(String username, String friendUsername);

    Set<String> getFriendsOf(String username);
}
