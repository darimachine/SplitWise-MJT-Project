package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.CurrencyConverter;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ObligationJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.helpers.servicerecords.ObligationDirection;

import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ObligationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ObligationServiceTest {

    private UserServiceAPI userServiceMock;
    private CurrencyConverter converterMock;
    private ObligationJsonProcessor processorMock;

    private ObligationService obligationService;

    private Map<String, Map<String, Double>> fakeObligations;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        converterMock = mock(CurrencyConverter.class);
        processorMock = mock(ObligationJsonProcessor.class);

        fakeObligations = new HashMap<>();
        when(processorMock.loadData()).thenReturn(fakeObligations);

        obligationService = new ObligationService(userServiceMock, converterMock, processorMock);
    }

    @Test
    void testGetAllObligations_InitiallyEmpty() {
        assertTrue(obligationService.getAllObligations().isEmpty(), "Should be empty at start");
    }

    @Test
    void testGetAllObligations_Immutable() {
        fakeObligations.put("alice", new HashMap<>(Map.of("bob", 50.0)));

        Map<String, Map<String, Double>> all = obligationService.getAllObligations();
        assertThrows(UnsupportedOperationException.class, () -> all.remove("alice"),
            "Should return unmodifiable map");
    }

    @Test
    void testGetAllObligationsForUser_NoEntry_ReturnsEmpty() {
        Map<String, Double> result = obligationService.getAllObligationsForUser("unknown");
        assertTrue(result.isEmpty(), "Should return empty map for unknown user");
    }

    @Test
    void testGetAllObligationsForUser_CopyReturned() {
        Map<String, Double> aliceMap = new HashMap<>(Map.of("bob", 100.0));
        fakeObligations.put("alice", aliceMap);

        Map<String, Double> retrieved = obligationService.getAllObligationsForUser("alice");
        assertEquals(1, retrieved.size());
        assertEquals(100.0, retrieved.get("bob"));

        retrieved.put("charlie", 200.0);
        assertFalse(aliceMap.containsKey("charlie"),
            "Original map should not be affected by changes to the returned copy");
    }

    @Test
    void testFindObligationBetweenUsers_NoExistingObligation_ReturnsNull() {
        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        ObligationDirection result = obligationService.findObligationBetweenUsers("alice", "bob");
        assertNull(result, "No obligations means null");
        verifyNoInteractions(converterMock);
    }

    @Test
    void testFindObligationBetweenUsers_ForwardObligation_ReturnsDirection() {
        fakeObligations.put("alice", Map.of("bob", 100.0));

        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.EUR);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        when(converterMock.convertFromBGN("EUR", 100.0)).thenReturn(50.0);

        ObligationDirection dir = obligationService.findObligationBetweenUsers("alice", "bob");
        assertNotNull(dir);
        assertEquals("alice", dir.debtor());
        assertEquals("bob", dir.creditor());
        assertEquals(50.0, dir.amount(), 1e-9);
        assertEquals("EUR", dir.currency());
    }

    @Test
    void testFindObligationBetweenUsers_ReverseObligation_ReturnsDirection() {
        fakeObligations.put("bob", Map.of("alice", 200.0));

        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.USD);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        when(converterMock.convertFromBGN("USD", 200.0)).thenReturn(100.0);

        ObligationDirection dir = obligationService.findObligationBetweenUsers("alice", "bob");
        assertNotNull(dir);
        assertEquals("bob", dir.debtor());
        assertEquals("alice", dir.creditor());
        assertEquals(100.0, dir.amount(), 1e-9);
        assertEquals("USD", dir.currency());
    }

    @Test
    void testAddObligation_NoReverseObligation_Forward() {
        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        bob.setCurrency(Currency.USD);
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        when(converterMock.convertToBGN("USD", 10.0)).thenReturn(18.0);

        obligationService.addObligation("alice", "bob", 10.0);
        assertTrue(fakeObligations.containsKey("alice"));
        assertEquals(18.0, fakeObligations.get("alice").get("bob"), 1e-9);

        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testAddObligation_ExistingReverseDebt_NetsOut() {
        fakeObligations.put("bob", new HashMap<>(Map.of("alice", 30.0)));

        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        bob.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        when(converterMock.convertToBGN("BGN", 20.0)).thenReturn(20.0);

        obligationService.addObligation("alice", "bob", 20.0);

        assertTrue(fakeObligations.containsKey("bob"));
        Map<String, Double> bobMap = fakeObligations.get("bob");
        assertEquals(10.0, bobMap.get("alice"), 1e-9);

        assertFalse(fakeObligations.containsKey("alice"));

        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testPayObligation_ReduceDebt() {
        fakeObligations.put("alice", new HashMap<>(Map.of("bob", 50.0)));

        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.EUR);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        when(converterMock.convertToBGN("EUR", 30.0)).thenReturn(60.0);

        obligationService.payObligation("alice", "bob", 20.0);
        assertEquals(60.0, fakeObligations.get("alice").get("bob"), 1e-9);
        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testPayObligation_FullyPays_RemovesRecord() {
        fakeObligations.put("alice", new HashMap<>(Map.of("bob", 50.0)));

        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        obligationService.payObligation("alice", "bob", 50.0);

        assertFalse(fakeObligations.containsKey("alice"));
        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testGetMyFriendsObligations_NoFriendsReturnsBasicString() {

        User alex = new User("alex", "123", "alexFirst", "alexLastName");
        alex.setCurrency(Currency.BGN);  // or any currency
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        String result = obligationService.getMyFriendsObligations("alex", Set.of());
        assertTrue(result.contains("Friends: [  ]"), "Should list no friends");
    }

    @Test
    void testGetMyFriendsObligations_HappyPath() {
        Map<String, Double> alexMap = new HashMap<>();
        alexMap.put("bob", 100.0);
        fakeObligations.put("alex", alexMap);

        Map<String, Double> johnMap = new HashMap<>();
        johnMap.put("alex", 40.0);
        fakeObligations.put("john", johnMap);

        User alex = new User("alex", "123", "alexFirst", "alexLastName");
        alex.setCurrency(Currency.USD);
        when(userServiceMock.getUser("alex")).thenReturn(alex);
        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        User john = new User("john", "123", "JohnFirst", "johnLastName");
        when(userServiceMock.getUser("john")).thenReturn(john);

        when(converterMock.convertFromBGN("USD", 100.0)).thenReturn(50.0);
        when(converterMock.convertFromBGN("USD", 40.0)).thenReturn(20.0);

        String result = obligationService.getMyFriendsObligations("alex", Set.of("bob", "john"));
        assertTrue(result.contains("Friends: ["), "Contains Friends:");
        assertTrue(result.contains("john"), "Contains john");
        assertTrue(result.contains("bob"), "Contains bob");
        assertTrue(result.contains("bob (BobFirst): You owe 50.00 USD"), "alex owes bob 50");
        assertTrue(result.contains("john (JohnFirst): Owes you 20.00 USD"), "john owes alex 20");
    }

    @Test
    void testGetMyGroupObligations_EmptyGroupsReturnsMessage() {
        String result = obligationService.getMyGroupObligations("alice", Collections.emptyMap());
        assertEquals("You are not in any group, or there are no group obligations.", result);
    }

    @Test
    void testGetMyGroupObligations_HappyPath() {
        Map<String, Set<String>> groups = new HashMap<>();
        groups.put("friendsGroup", Set.of("alex", "bob", "john"));

        Map<String, Double> bobMap = new HashMap<>();
        bobMap.put("alex", 100.0);
        fakeObligations.put("bob", bobMap);

        Map<String, Double> alexMap = new HashMap<>();
        alexMap.put("john", 40.0);
        fakeObligations.put("alex", alexMap);
        User alex = new User("alex", "123", "alexFirst", "alexLastName");
        alex.setCurrency(Currency.USD);
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        User john = new User("john", "123", "JohnFirst", "johnLastName");
        when(userServiceMock.getUser("john")).thenReturn(john);
        when(converterMock.convertFromBGN("USD", 100.0)).thenReturn(50.0); // bob->alex => $50
        when(converterMock.convertFromBGN("USD", 40.0)).thenReturn(20.0);  // alex->john => $20

        String result = obligationService.getMyGroupObligations("alex", groups);
        assertTrue(result.contains("==== Group: friendsGroup ==="), "Should display group name");
        assertTrue(result.contains("bob (BobFirst): Owes you 50.00 USD"), "bob->alex $50");
        assertTrue(result.contains("john (JohnFirst): You owe 20.00 USD"), "alex->john $20");
    }
}
