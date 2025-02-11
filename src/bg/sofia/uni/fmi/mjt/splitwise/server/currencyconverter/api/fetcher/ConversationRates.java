package bg.sofia.uni.fmi.mjt.splitwise.server.currencyconverter.api.fetcher;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public record ConversationRates(@SerializedName("conversion_rates") Map<String, Double> conversionRates) {
}
