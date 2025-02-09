package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import java.util.Map;
import java.util.Set;

public interface GroupServiceAPI {

    Map<String, Set<String>> getGroups();

    /**
     * Creates a new group with the given name and members.
     * If the group already exists, throws an exception or updates.
     */
    void createGroup(String groupName, Set<String> members);

    /**
     * Returns the set of members for the specified group.
     */
    Set<String> getGroupMembers(String groupName);

    /**
     * Checks if user is a member of groupName.
     */
    boolean isMember(String groupName, String username);

    /**
     * Adds a single user to an existing group.
     */
    void addUserToGroup(String groupName, String username);

    /**
     * Removes a single user from an existing group.
     */
    void removeUserFromGroup(String groupName, String username);

    /**
     * Returns all group names where the given user participates.
     */
    Set<String> getUserGroups(String username);
}
