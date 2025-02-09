package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ObligationJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ObligationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ObligationServiceTest {
    private UserServiceAPI userService;
    private ObligationJsonProcessor processor;
    private ObligationService obligationService;


    @BeforeEach
    void setUp() {
        userService = mock();
        processor = mock();

        Map<String, Map<String, Double>> mockObligations = new HashMap<>();
        mockObligations.put("alex",new HashMap<>(Map.of("maria", 10.0)));
        mockObligations.put("maria",new HashMap<>(Map.of("john", 5.0)));
        when(processor.loadData()).thenReturn(mockObligations);

        when(userService.getUser("alex")).thenReturn(new User("alex", "Alex", "Smith","1234"));
        when(userService.getUser("maria")).thenReturn(new User("maria", "Maria", "Johnson","1234"));
        when(userService.getUser("john")).thenReturn(new User("john", "John", "Doe","1234"));

        obligationService = new ObligationService(userService, processor);
    }

    @Test
    void testAddObligation_NewEntry_ShouldCreateObligation() {
        obligationService.addObligation("peter", "john", 7.0);
        double newObligation = obligationService.findObligationBetweenUsers("peter", "john");
        assertEquals(7.0, newObligation, "New obligation should be created with 7.0");
        verify(processor, times(1)).saveData(any());
    }

//    @Test
//    void testAddObligation_SameUser_ShouldDoNothing() {
//        obligationService.addObligation("alex", "alex", 10.0);
//        double obligation = obligationService.findObligationBetweenUsers("alex", "alex");
//        assertEquals(0.0, obligation, "Adding obligation to self should do nothing.");
//    }
    @Test
    void testAddObligation_ZeroAmount_ShouldDoNothing() {
        obligationService.addObligation("alex", "maria", 0.0);
        double obligation = obligationService.findObligationBetweenUsers("alex", "maria");
        assertEquals(10.0, obligation, "Adding zero obligation should not change anything.");
    }
    @Test
    void testAddObligation_ExistingObligation_ShouldAccumulate() {
        obligationService.addObligation("alex", "maria", 5.0);
        double updatedObligation = obligationService.findObligationBetweenUsers("alex", "maria");
        assertEquals(15.0, updatedObligation, "Obligation should accumulate correctly");
        verify(processor, times(1)).saveData(any());
    }

    @Test
    void testPayObligation_PartialPayment_ShouldReduceObligation() {
        obligationService.payObligation("alex", "maria", 5.0);
        double remainingObligation = obligationService.findObligationBetweenUsers("alex", "maria");
        assertEquals(5.0, remainingObligation, "Obligation should be reduced by payment amount");
        verify(processor, times(1)).saveData(any());
    }

    @Test
    void testPayObligation_FullPayment_ShouldRemoveObligation() {
        obligationService.payObligation("alex", "maria", 10.0);
        double remainingObligation = obligationService.findObligationBetweenUsers("alex", "maria");
        assertEquals(0.0, remainingObligation, "Obligation should be removed after full payment");
        verify(processor, times(1)).saveData(any());
    }

//    @Test
//    void testPayObligation_WhenAmountExceeds_ShouldThrowException() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            obligationService.payObligation("alex", "maria", 15.0);
//        }, "Should throw an exception if paying more than owed");
//    } in the validator!!

    @Test
    void testFindObligationBetweenUsers_ShouldReturnCorrectValue() {
        double obligation = obligationService.findObligationBetweenUsers("alex", "maria");
        assertEquals(10.0, obligation, "Should return correct obligation amount");
    }

    @Test
    void testGetAllObligationsForUser_ShouldReturnCorrectObligations() {
        Map<String, Double> obligations = obligationService.getAllObligationsForUser("maria");
        assertEquals(1, obligations.size(), "Should return correct obligations count");
        assertEquals(5.0, obligations.get("john"), "Should return correct obligation amount");
    }

    @Test
    void testGetAllObligationsForUser_WhenUserHasNoObligations_ShouldReturnEmptyMap() {
        Map<String, Double> obligations = obligationService.getAllObligationsForUser("unknownUser");
        assertTrue(obligations.isEmpty(), "Should return empty map for users without obligations");
    }

    @Test
    void testGetMyFriendsObligations_ShouldReturnOnlyFriends() {
        Set<String> friends = new HashSet<>(List.of("maria"));

        String output = obligationService.getMyFriendsObligations("alex", friends);

        System.out.println(output);
        assertTrue(output.contains("maria") && !output.contains("john"), "Output should contain only friends' obligations");
    }

    @Test
    void testGetMyGroupObligations_ShouldReturnOnlyGroups() {
        Map<String, Set<String>> groups = new HashMap<>();
        groups.put("Roommates", new HashSet<>(Arrays.asList("alex", "maria", "john")));

        String output = obligationService.getMyGroupObligations("alex", groups);
        System.out.println(output);
        assertTrue(output.contains("Roommates") && output.contains("maria"), "Output should contain only group obligations");
    }

    @Test
    void testGetMyGroupObligations_GroupMembersObligations() {
        User alice = mock(User.class);
        when(alice.getFirstName()).thenReturn("Alice");
        User bob = mock(User.class);
        when(bob.getFirstName()).thenReturn("Bob");
        User charlie = mock(User.class);
        when(charlie.getFirstName()).thenReturn("Charlie");

        when(userService.getUser("alice")).thenReturn(alice);
        when(userService.getUser("bob")).thenReturn(bob);
        when(userService.getUser("charlie")).thenReturn(charlie);

        Map<String, Map<String, Double>> initial = new HashMap<>();
        initial.put("alice", new HashMap<>(Map.of("bob", 10.0)));
        initial.put("charlie", new HashMap<>(Map.of("alice", 20.0)));
        when(processor.loadData()).thenReturn(initial);
        obligationService = new ObligationService(userService, processor);
        when(alice.getPreferredCurrency()).thenReturn(Currency.BGN);
        String result = obligationService.getMyGroupObligations("alice", Map.of("group1", Set.of("alice", "bob", "charlie")));

        assertTrue(result.contains("group1"));
        assertTrue(result.contains("Bob"));
        assertTrue(result.contains("You owe 10.00"));
        assertTrue(result.contains("Charlie"));
        assertTrue(result.contains("Owes you 20.00"));
    }

}
