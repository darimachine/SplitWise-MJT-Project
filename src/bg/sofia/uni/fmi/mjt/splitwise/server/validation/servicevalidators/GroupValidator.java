package bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.GroupAlreadyExistException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.GroupDoesntExistsException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.GroupMinimumMemberException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.UserAlreadyPartInTheGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.UserNotInGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.users.UserNotInAnyGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;

import java.util.Set;

public class GroupValidator {

    private final GroupServiceAPI groupService;
    private static final int MINIMUM_GROUP_MEMBERS = 2;

    public GroupValidator(GroupServiceAPI groupService) {
        this.groupService = groupService;
    }

    public void validateGroupSize(Set<String> members) {
        if (members.size() < MINIMUM_GROUP_MEMBERS) {
            throw new GroupMinimumMemberException("Group must have at least 3 members!");
        }
    }

    public void validateGroupName(String groupName) {
        if (groupName == null || groupName.isBlank()) {
            throw new IllegalArgumentException("Group name cannot be null or empty!");
        }
    }

    public void validateUserNotInGroup(String groupName, String username) {
        if (groupService.getGroups().get(groupName).contains(username)) {
            throw new UserAlreadyPartInTheGroupException(
                "User with username " + username + " is already part of group " + groupName);
        }
    }

    public void validateUserInsideGroup(String groupName, String username) {
        if (!groupService.getGroups().get(groupName).contains(username)) {
            throw new UserNotInGroupException("User with username " + username + " is not part of group " + groupName);
        }
    }

    public void isUserInAnyGroup(String username) {
        for (var group : groupService.getGroups().values()) {
            if (group.contains(username)) {
                return;
            }
        }
        throw new UserNotInAnyGroupException("User with username " + username + " is not part of any group!");
    }

    public void validateGroupExists(String groupName) {
        if (!groupService.getGroups().containsKey(groupName)) {
            throw new GroupDoesntExistsException("Group with name " + groupName + " does not exist!");
        }
    }

    public void validateGroupDoesNotExist(String groupName) {
        if (groupService.getGroups().containsKey(groupName)) {
            throw new GroupAlreadyExistException("Group with name " + groupName + " already exists!");
        }
    }

}
