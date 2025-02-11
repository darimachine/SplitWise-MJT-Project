package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.NotificationJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.NotificationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class NotificationServiceTest {

    private NotificationJsonProcessor processorMock;
    private NotificationService notificationService;
    private Map<String, List<Notification>> fakeNotifications;

    @BeforeEach
    void setUp() {
        processorMock = mock(NotificationJsonProcessor.class);

        fakeNotifications = new HashMap<>();
        when(processorMock.loadData()).thenReturn(fakeNotifications);
        notificationService = new NotificationService(processorMock);
    }

    @Test
    void testGetAllNotificationsForUser_NoSuchUser_ReturnsEmptyList() {
        List<Notification> result = notificationService.getAllNotificationsForUser("alice");
        assertTrue(result.isEmpty(), "Should be empty for unknown user");
    }

    @Test
    void testGetAllNotificationsForUser_NonEmpty() {

        List<Notification> bobNotes = new ArrayList<>();
        bobNotes.add(new Notification("Msg1"));
        bobNotes.add(new Notification("Msg2"));
        fakeNotifications.put("bob", bobNotes);

        List<Notification> result = notificationService.getAllNotificationsForUser("bob");
        assertEquals(2, result.size(), "Should return 2 notifications");
        assertEquals("Msg1", result.get(0).getMessage());
        assertEquals("Msg2", result.get(1).getMessage());
    }

    @Test
    void testGetUnseenNotificationsForUser_OnlyReturnsUnseen() {
        // Suppose "charlie" has 3 notifications, 1 is seen, 2 are unseen
        Notification n1 = new Notification("First unseen");
        Notification n2 = new Notification("Seen note");
        Notification n3 = new Notification("Second unseen");

        n2.markAsSeen();

        List<Notification> charlieNotes = new ArrayList<>(List.of(n1, n2, n3));
        fakeNotifications.put("charlie", charlieNotes);

        List<Notification> unseen = notificationService.getUnseenNotificationsForUser("charlie");
        assertEquals(2, unseen.size(), "Should return the 2 unseen notifications");
        assertTrue(unseen.contains(n1), "Should contain 'First unseen'");
        assertTrue(unseen.contains(n3), "Should contain 'Second unseen'");
        assertFalse(unseen.contains(n2), "Should not contain the seen one");
    }

    @Test
    void testGetNotificationsToString_NullOrEmptyInput_ReturnsEmptyString() {
        String result1 = notificationService.getNotificationsToString(null);
        assertEquals("", result1, "If null list, should return empty string");

        String result2 = notificationService.getNotificationsToString(List.of());
        assertEquals("", result2, "If empty list, should return empty string");
    }

    @Test
    void testGetNotificationsToString_NonEmpty() {
        List<Notification> notes = new ArrayList<>();
        notes.add(new Notification("Msg1"));
        notes.add(new Notification("Msg2"));

        String result = notificationService.getNotificationsToString(notes);
        assertTrue(result.contains("*** Notifications ***"), "Should contain notifications header");
        assertTrue(result.contains("Msg1"), "Should contain Msg1");
        assertTrue(result.contains("Msg2"), "Should contain Msg2");
    }

    @Test
    void testMarkNotificationsAsSeen_EmptyUserListOrEmptyNotifications_NoSaveDataCall() {

        notificationService.markNotificationsAsSeen("alice", List.of(new Notification("whatever")));
        verify(processorMock, never()).saveData(anyMap());

        fakeNotifications.put("bob", new ArrayList<>(List.of(new Notification("hey"))));
        notificationService.markNotificationsAsSeen("bob", null);
        verify(processorMock, never()).saveData(anyMap());
    }

    @Test
    void testMarkNotificationsAsSeen_MarksAndSaves() {

        Notification n1 = new Notification("N1");
        Notification n2 = new Notification("N2");
        fakeNotifications.put("alex", new ArrayList<>(List.of(n1, n2)));

        notificationService.markNotificationsAsSeen("alex", List.of(n1));

        assertTrue(n1.isSeen());

        assertFalse(n2.isSeen());
        verify(processorMock).saveData(fakeNotifications);
    }

    @Test
    void testAddNotification_SingleRecipient_NoExistingNotifications() {

        notificationService.addNotification("Some message", "alice");

        assertTrue(fakeNotifications.containsKey("alice"), "alice should be in the map");
        List<Notification> aliceNotes = fakeNotifications.get("alice");
        assertEquals(1, aliceNotes.size());
        assertEquals("Some message", aliceNotes.getFirst().getMessage());

        verify(processorMock).saveData(fakeNotifications);
    }

    @Test
    void testAddNotification_SingleRecipient_AlreadyExists() {
        // Suppose bob has an existing list
        fakeNotifications.put("bob", new ArrayList<>(List.of(new Notification("Old msg"))));

        notificationService.addNotification("New msg", "bob");

        List<Notification> bobList = fakeNotifications.get("bob");
        assertEquals(2, bobList.size());
        assertEquals("New msg", bobList.get(1).getMessage());

        verify(processorMock).saveData(fakeNotifications);
    }

    @Test
    void testAddNotification_MultipleRecipients() {
        Set<String> recipients = Set.of("alice", "bob");

        notificationService.addNotification("Hello all", recipients);

        // "alice" => [ Notification("Hello all") ]
        // "bob" => [ Notification("Hello all") ]
        assertTrue(fakeNotifications.containsKey("alice"));
        assertTrue(fakeNotifications.containsKey("bob"));

        assertEquals(1, fakeNotifications.get("alice").size());
        assertEquals(1, fakeNotifications.get("bob").size());
        assertEquals("Hello all", fakeNotifications.get("alice").get(0).getMessage());

        verify(processorMock, times(2)).saveData(fakeNotifications);
        // Because for each recipient, it calls addNotification => each time calls saveData
    }

    @Test
    void testAddNotification_RecipientsEmpty_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
            () -> notificationService.addNotification("Msg", (Set<String>) null),
            "Should throw if recipientsUsernames is null");

        assertThrows(IllegalArgumentException.class,
            () -> notificationService.addNotification("Msg", Set.of()),
            "Should throw if recipientsUsernames is empty");
    }
}
