package bg.sofia.uni.fmi.mjt.splitwise.server.helpers.formatters;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.CurrencyConverter;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ObligationFormatterTest {

    private UserServiceAPI userServiceMock;
    private CurrencyConverter currencyConverterMock;
    private ObligationFormatter formatter;

    @BeforeEach
    void setUp() {
        userServiceMock = mock(UserServiceAPI.class);
        currencyConverterMock = mock(CurrencyConverter.class);
        formatter = new ObligationFormatter(userServiceMock, currencyConverterMock);
    }

    @Test
    void testFormatObligations_BothEmpty() {
        User alex = new User("alex", "123", "AlexFirst", "AlexLast");
        alex.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        String result = formatter.formatObligations(Map.of(), Map.of(), "alex");

        assertTrue(result.contains("Nobody owes me money"), "Expected message about nobody owing me");
        assertTrue(result.contains("You don't owe money to anyone"), "Expected message about I owe nobody");
    }

    @Test
    void testFormatObligations_PeopleWhoOweMeNonEmpty_EmptyPeopleWhoIOwe() {
        User alex = new User("alex", "123", "AlexFirst", "AlexLast");
        alex.setCurrency(Currency.EUR);
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        Map<String, Double> oweMeMap = Map.of("bob", 100.0);
        Map<String, Double> iOweMap = Map.of();

        User bob = new User("bob", "456", "BobFirst", "BobLast");
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        when(currencyConverterMock.convertFromBGN("EUR", 100.0)).thenReturn(50.0);

        String result = formatter.formatObligations(oweMeMap, iOweMap, "alex");

        assertTrue(result.contains("bob (BobFirst): Owes you 50.00 EUR"), "Expected bob owes me 50 EUR");
        assertTrue(result.contains("You don't owe money to anyone"), "Expected no debts from me");
        assertFalse(result.contains("Nobody owes me money"), "Should not mention 'nobody owes me' if it's non-empty");
    }

    @Test
    void testFormatObligations_EmptyPeopleWhoOweMe_PeopleWhoIOweNonEmpty() {
        User alex = new User("alex", "123", "AlexFirst", "AlexLast");
        alex.setCurrency(Currency.USD);
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        Map<String, Double> oweMeMap = Map.of();
        Map<String, Double> iOweMap = Map.of("john", 70.0);

        User john = new User("john", "123", "JohnFirst", "JohnLast");
        when(userServiceMock.getUser("john")).thenReturn(john);

        when(currencyConverterMock.convertFromBGN("USD", 70.0)).thenReturn(35.0);

        String result = formatter.formatObligations(oweMeMap, iOweMap, "alex");

        assertTrue(result.contains("john (JohnFirst): You owe 35.00 USD"), "Expected I owe john 35 USD");
        assertTrue(result.contains("Nobody owes me money"), "Expected empty oweMe => nobody owes me msg");
    }

    @Test
    void testFormatObligations_BothNonEmpty() {
        User alex = new User("alex", "123", "AlexFirst", "AlexLast");
        alex.setCurrency(Currency.BGN);
        when(userServiceMock.getUser("alex")).thenReturn(alex);

        Map<String, Double> oweMeMap = Map.of("bob", 30.0);
        Map<String, Double> iOweMap = Map.of("john", 40.0);

        User bob = new User("bob", "456", "BobFirst", "BobLast");
        when(userServiceMock.getUser("bob")).thenReturn(bob);

        User john = new User("john", "789", "JohnFirst", "JohnLast");
        when(userServiceMock.getUser("john")).thenReturn(john);

        when(currencyConverterMock.convertFromBGN("BGN", 30.0)).thenReturn(30.0);
        when(currencyConverterMock.convertFromBGN("BGN", 40.0)).thenReturn(40.0);

        String result = formatter.formatObligations(oweMeMap, iOweMap, "alex");

        assertTrue(result.contains("bob (BobFirst): Owes you 30.00 BGN"), "Expected bob owes me 30 BGN");
        assertTrue(result.contains("john (JohnFirst): You owe 40.00 BGN"), "Expected I owe john 40 BGN");
    }
}
