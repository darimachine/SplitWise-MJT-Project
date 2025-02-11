package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.GroupJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GroupServiceTest {

    private GroupJsonProcessor processorMock;
    private GroupService groupService;
    private Map<String, Set<String>> fakeGroups; // in-memory fake data

    @BeforeEach
    void setUp() {
        processorMock = mock(GroupJsonProcessor.class);
        fakeGroups = new HashMap<>();
        when(processorMock.loadData()).thenReturn(fakeGroups);
        groupService = new GroupService(processorMock);
    }

    @Test
    void testGetGroups_InitiallyEmpty() {
        assertTrue(groupService.getGroups().isEmpty(), "Groups should be empty initially.");
    }

    @Test
    void testGetGroups_Immutable() {
        fakeGroups.put("group1", new HashSet<>(Set.of("alice", "bob")));

        Map<String, Set<String>> result = groupService.getGroups();
        assertThrows(UnsupportedOperationException.class, () -> result.remove("group1"),
            "Should return an unmodifiable map.");
    }

    @Test
    void testCreateGroup_SavesData() {
        Set<String> members = new HashSet<>(Set.of("alice", "bob"));
        groupService.createGroup("newGroup", members);

        assertTrue(fakeGroups.containsKey("newGroup"), "New group should be created.");
        assertEquals(members, fakeGroups.get("newGroup"), "Members should be saved correctly.");
        verify(processorMock).saveData(fakeGroups);
    }

    @Test
    void testGetGroupMembers_ExistingGroup() {
        fakeGroups.put("team", new HashSet<>(Set.of("alice", "bob", "charlie")));

        Set<String> members = groupService.getGroupMembers("team");
        assertEquals(3, members.size(), "Should return correct number of members.");
        assertTrue(members.containsAll(Set.of("alice", "bob", "charlie")), "All members should be present.");
    }

    @Test
    void testGetGroupMembers_NonExistentGroup_ReturnsNull() {
        assertNull(groupService.getGroupMembers("unknownGroup"), "Should return null for non-existent group.");
    }

    @Test
    void testIsMember_True() {
        fakeGroups.put("studyGroup", new HashSet<>(Set.of("alex", "peter")));
        assertTrue(groupService.isMember("studyGroup", "alex"), "Alex should be in the group.");
    }

    @Test
    void testIsMember_FalseForMissingGroup() {
        assertFalse(groupService.isMember("studyGroup", "alex"), "Should return false if group does not exist.");
    }

    @Test
    void testIsMember_FalseIfUserNotInGroup() {
        fakeGroups.put("studyGroup", new HashSet<>(Set.of("alex")));
        assertFalse(groupService.isMember("studyGroup", "bob"), "Bob is not a member.");
    }

    @Test
    void testAddUserToGroup_SavesData() {
        fakeGroups.put("band", new HashSet<>(Set.of("alice")));
        groupService.addUserToGroup("band", "bob");

        assertTrue(fakeGroups.get("band").contains("bob"), "Bob should be added.");
        verify(processorMock).saveData(fakeGroups);
    }


    @Test
    void testGetUserGroups_NoGroups() {
        Set<String> result = groupService.getUserGroups("unknown");
        assertTrue(result.isEmpty(), "Should return empty set for user with no groups.");
    }

    @Test
    void testGetUserGroups_FindsAllGroups() {
        fakeGroups.put("sports", new HashSet<>(Set.of("alex", "bob")));
        fakeGroups.put("music", new HashSet<>(Set.of("alex", "charlie")));
        fakeGroups.put("code", new HashSet<>(Set.of("bob", "charlie")));

        Set<String> groupsForAlex = groupService.getUserGroups("alex");
        assertEquals(2, groupsForAlex.size(), "Alex should be in two groups.");
        assertTrue(groupsForAlex.containsAll(Set.of("sports", "music")), "Groups should match.");
    }

    @Test
    void testRemoveGroup_Success() {
        fakeGroups.put("hiking", new HashSet<>(Set.of("alice", "bob")));
        groupService.removeGroup("hiking");

        assertFalse(fakeGroups.containsKey("hiking"), "Group should be removed.");
        verify(processorMock).saveData(fakeGroups);
    }

}
