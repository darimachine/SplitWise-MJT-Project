package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher;

import bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.request.ExchangeRateURI;
import bg.sofia.uni.fmi.mjt.splitwise.exceptions.api.RequestFailedException;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.gson.GsonSingleton;
import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ExchangeRateFetcher {
    private final HttpClient httpClient;
    private final Gson gson = GsonSingleton.getInstance();
    private static final int RESPONSE_OK = 200;

    public ExchangeRateFetcher() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public ExchangeRateFetcher(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public ConversationRates fetchRates(String baseCurrency) {
        URI uri = ExchangeRateURI.newBuilder(baseCurrency).build().getUri();
        HttpResponse<String> response = sendRequest(uri);
        if (response.statusCode() != RESPONSE_OK) {
            throw new RequestFailedException("Bad response code: " + response.statusCode());
        }
        return gson.fromJson(response.body(), ConversationRates.class);
    }

    private HttpResponse<String> sendRequest(URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(uri)
            .GET()
            .build();
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RequestFailedException("Failed to send request", e);
        }
    }
}
