package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.GroupJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupService implements GroupServiceAPI {

    private final GroupJsonProcessor processor;
    private final Map<String, Set<String>> groups;

    public GroupService(GroupJsonProcessor processor) {
        this.processor = processor;
        this.groups = processor.loadData();
    }

    @Override
    public Map<String, Set<String>> getGroups() {
        return Collections.unmodifiableMap(groups);
    }

    @Override
    public void createGroup(String groupName, Set<String> members) {
        groups.put(groupName, members);
        processor.saveData(groups);
    }

    @Override
    public Set<String> getGroupMembers(String groupName) {
        return groups.get(groupName);
    }

    @Override
    public boolean isMember(String groupName, String username) {
        Set<String> memberSet = groups.get(groupName);
        if (memberSet == null) {
            return false;
        }
        return memberSet.contains(username);
    }

    @Override
    public void addUserToGroup(String groupName, String username) {
        Set<String> memberSet = groups.get(groupName);
        memberSet.add(username); // modifies in-memory set
        processor.saveData(groups);
    }

    @Override
    public Set<String> getUserGroups(String username) {
        return groups.entrySet().stream()
            .filter(e -> e.getValue().contains(username))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeGroup(String groupName) {
        groups.remove(groupName);
        processor.saveData(groups);
    }
}
