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

    // We'll store a fake in-memory obligations map that the service will manipulate
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
        // Suppose "alice" -> "bob"=100
        Map<String, Double> aliceMap = new HashMap<>(Map.of("bob", 100.0));
        fakeObligations.put("alice", aliceMap);

        Map<String, Double> retrieved = obligationService.getAllObligationsForUser("alice");
        assertEquals(1, retrieved.size());
        assertEquals(100.0, retrieved.get("bob"));

        // ensure it's a copy
        retrieved.put("charlie", 200.0);
        assertFalse(aliceMap.containsKey("charlie"),
            "Original map should not be affected by changes to the returned copy");
    }

    @Test
    void testFindObligationBetweenUsers_NoExistingObligation_ReturnsNull() {
        // "alice" has no obligations with "bob"
        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        ObligationDirection result = obligationService.findObligationBetweenUsers("alice", "bob");
        assertNull(result, "No obligations means null");
        verifyNoInteractions(converterMock);
    }

    @Test
    void testFindObligationBetweenUsers_ForwardObligation_ReturnsDirection() {
        // "alice" -> "bob"=100 BGN
        fakeObligations.put("alice", Map.of("bob", 100.0));

        // Suppose alice is in EUR
        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.EUR);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        // Convert 100 BGN => 50 EUR
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
        // "bob"->"alice"=200 BGN
        fakeObligations.put("bob", Map.of("alice", 200.0));

        // fromUser="alice" => forward=0, reverse=200 => => "bob owes alice"
        // Suppose "alice" is in USD
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
        // "alice" owes "bob" 10 in "bob"'s currency
        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        bob.setCurrency(Currency.USD);
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        // Suppose converter => 10 "USD" => 18 BGN
        when(converterMock.convertToBGN("USD", 10.0)).thenReturn(18.0);

        obligationService.addObligation("alice", "bob", 10.0);
        assertTrue(fakeObligations.containsKey("alice"));
        assertEquals(18.0, fakeObligations.get("alice").get("bob"), 1e-9);

        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testAddObligation_ExistingReverseDebt_NetsOut() {
        // "bob" -> "alice"=30 BGN
        fakeObligations.put("bob", new HashMap<>(Map.of("alice", 30.0)));

        // Now we add "alice"->"bob"=20 in bob's currency => convert => let's say 20 BGN
        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        bob.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        when(converterMock.convertToBGN("BGN", 20.0)).thenReturn(20.0);

        obligationService.addObligation("alice", "bob", 20.0);

        // The net => 30 - 20 = 10 => remains bob->alice
        assertTrue(fakeObligations.containsKey("bob"));
        Map<String, Double> bobMap = fakeObligations.get("bob");
        assertEquals(10.0, bobMap.get("alice"), 1e-9);

        // "alice" map shouldn't exist if net is still in "bob->alice"
        assertFalse(fakeObligations.containsKey("alice"));

        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testPayObligation_ReduceDebt() {
        // "alice"->"bob"=50 BGN
        fakeObligations.put("alice", new HashMap<>(Map.of("bob", 50.0)));

        // "alice" has EUR
        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.EUR);
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        // payObligation does:
        // oldValue=50, newVal=30 => convertToBGN(EUR,30)= e.g. 60 => store "alice->bob"=60
        when(converterMock.convertToBGN("EUR", 30.0)).thenReturn(60.0);

        obligationService.payObligation("alice", "bob", 20.0);
        // now the map has "alice->bob"=60
        assertEquals(60.0, fakeObligations.get("alice").get("bob"), 1e-9);
        verify(processorMock).saveData(fakeObligations);
    }

    @Test
    void testPayObligation_FullyPays_RemovesRecord() {
        // "alice"->"bob"=50 BGN
        fakeObligations.put("alice", new HashMap<>(Map.of("bob", 50.0)));

        User alice = new User("alice", "123", "AliceFirst", "AliceLast");
        alice.setCurrency(Currency.BGN); // simpler
        when(userServiceMock.getUser("alice")).thenReturn(alice);

        // if newVal=0 => convertToBGN(BGN,0)=0 => remove
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
        // Possibly also verify the "Nobody owes me" lines etc.
    }

    @Test
    void testGetMyFriendsObligations_HappyPath() {
        // Suppose "alex" -> "bob"=100 BGN, and "john"->"alex"=40 BGN
        Map<String, Double> alexMap = new HashMap<>();
        alexMap.put("bob", 100.0); // alex owes bob
        fakeObligations.put("alex", alexMap);

        Map<String, Double> johnMap = new HashMap<>();
        johnMap.put("alex", 40.0); // john owes alex
        fakeObligations.put("john", johnMap);

        // "alex" has some currency
        User alex = new User("alex", "123", "alexFirst", "alexLastName");
        alex.setCurrency(Currency.USD);
        when(userServiceMock.getUser("alex")).thenReturn(alex);
        // "bob" => to fetch firstName or similar
        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        User john = new User("john", "123", "JohnFirst", "johnLastName");
        when(userServiceMock.getUser("john")).thenReturn(john);

        // Suppose convertFromBGN(USD,100)=50 => alex owes bob $50
        when(converterMock.convertFromBGN("USD", 100.0)).thenReturn(50.0);
        // Suppose convertFromBGN(USD,40)=20 => john owes alex $20
        when(converterMock.convertFromBGN("USD", 40.0)).thenReturn(20.0);

        String result = obligationService.getMyFriendsObligations("alex", Set.of("bob", "john"));
        // Check the formatted output
        assertTrue(result.contains("Friends: ["), "Contains Friends:");
        assertTrue(result.contains("john"), "Contains john");
        assertTrue(result.contains("bob"), "Contains bob");
        //doesnt work because its set and order is not guaranteed
        //assertTrue(result.contains("Friends: [ bob, john ]"), "List of 2 friends");
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
        // "alex" is in a group "friendsGroup" with "bob" and "john"
        Map<String, Set<String>> groups = new HashMap<>();
        groups.put("friendsGroup", Set.of("alex", "bob", "john"));

        // "bob"->"alex"=100 BGN, "alex"->"john"=40 BGN => just to see how it prints
        Map<String, Double> bobMap = new HashMap<>();
        bobMap.put("alex", 100.0);
        fakeObligations.put("bob", bobMap);

        Map<String, Double> alexMap = new HashMap<>();
        alexMap.put("john", 40.0);
        fakeObligations.put("alex", alexMap);

        // "alex" in USD
        User alex = new User("alex", "123", "alexFirst", "alexLastName");
        alex.setCurrency(Currency.USD);
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        User bob = new User("bob", "123", "BobFirst", "bobLastName");
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        User john = new User("john", "123", "JohnFirst", "johnLastName");
        when(userServiceMock.getUser("john")).thenReturn(john);
        // currency
        when(converterMock.convertFromBGN("USD", 100.0)).thenReturn(50.0); // bob->alex => $50
        when(converterMock.convertFromBGN("USD", 40.0)).thenReturn(20.0);  // alex->john => $20

        String result = obligationService.getMyGroupObligations("alex", groups);
        // Should have "==== Group: friendsGroup ===" and lines about bob->alex, alex->john
        assertTrue(result.contains("==== Group: friendsGroup ==="), "Should display group name");
        assertTrue(result.contains("bob (BobFirst): Owes you 50.00 USD"), "bob->alex $50");
        assertTrue(result.contains("john (JohnFirst): You owe 20.00 USD"), "alex->john $20");
    }
}
