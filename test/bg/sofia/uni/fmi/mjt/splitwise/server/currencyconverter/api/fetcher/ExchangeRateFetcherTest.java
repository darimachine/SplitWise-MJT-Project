package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.api.RequestFailedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.request.ExchangeRateURI;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExchangeRateFetcherTest {

    private static final String BASE_CURRENCY = "USD";
    private static final String RESPONSE_BODY = """
        {
            "conversion_rates": {
                "USD": 1.0,
                "EUR": 0.85,
                "GBP": 0.75
            }
        }
        """;

    private ExchangeRateFetcher fetcher;
    private HttpClient httpClientMock;
    private HttpResponse<String> httpResponseMock;

    @BeforeEach
    void setUp() {
        httpClientMock = mock(HttpClient.class);
        httpResponseMock = (HttpResponse<String>) mock(HttpResponse.class);
        fetcher = new ExchangeRateFetcher(httpClientMock);
    }

    @Test
    void testFetchRates_SuccessfulResponse() throws Exception {
        // Mock response for successful API call
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(RESPONSE_BODY);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(
            httpResponseMock);

        ConversationRates rates = fetcher.fetchRates(BASE_CURRENCY);

        assertNotNull(rates, "The fetched rates should not be null.");
        assertEquals(1.0, rates.conversionRates().get("USD"), "USD conversion rate should be 1.0.");
        assertEquals(0.85, rates.conversionRates().get("EUR"), "EUR conversion rate should be 0.85.");
        assertEquals(0.75, rates.conversionRates().get("GBP"), "GBP conversion rate should be 0.75.");
    }

    @Test
    void testFetchRates_Non200ResponseCode_ThrowsException() throws Exception {
        // Mock non-200 response
        when(httpResponseMock.statusCode()).thenReturn(400);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(
            httpResponseMock);

        assertThrows(RequestFailedException.class, () -> fetcher.fetchRates(BASE_CURRENCY),
            "Should throw RequestFailedException when response code is not 200.");
    }

    @Test
    void testFetchRates_RequestFails_ThrowsException() throws Exception {
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(
            new RuntimeException("Network error"));

        assertThrows(RequestFailedException.class, () -> fetcher.fetchRates(BASE_CURRENCY),
            "Should throw RequestFailedException when an exception occurs during request.");
    }

    @Test
    void testFetchRates_ValidatesRequestURI() throws Exception {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(RESPONSE_BODY);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(
            httpResponseMock);

        fetcher.fetchRates(BASE_CURRENCY);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClientMock).send(requestCaptor.capture(), any());

        HttpRequest capturedRequest = requestCaptor.getValue();
        URI expectedUri = ExchangeRateURI.newBuilder(BASE_CURRENCY).build().getUri();

        assertEquals(expectedUri, capturedRequest.uri(),
            "The request URI should match the expected exchange rate URI.");
        assertEquals("GET", capturedRequest.method(), "The request method should be GET.");
    }

    @Test
    void testFetchRates_ParsesResponseCorrectly() throws Exception {
        when(httpResponseMock.statusCode()).thenReturn(200);
        when(httpResponseMock.body()).thenReturn(RESPONSE_BODY);
        when(httpClientMock.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(
            httpResponseMock);

        ConversationRates rates = fetcher.fetchRates(BASE_CURRENCY);
        Map<String, Double> rateMap = rates.conversionRates();

        assertEquals(3, rateMap.size(), "There should be exactly 3 conversion rates.");
        assertTrue(rateMap.containsKey("USD"), "Response should contain USD conversion rate.");
        assertTrue(rateMap.containsKey("EUR"), "Response should contain EUR conversion rate.");
        assertTrue(rateMap.containsKey("GBP"), "Response should contain GBP conversion rate.");
    }
}
