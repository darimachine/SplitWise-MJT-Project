package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

public class ObligationJsonProcessor extends AbstractJsonProcessor<Map<String, Map<String, Double>>> {
    private static final String OBLIGATION_FILE_PATH = "resources/obligations.json";
    private static final Type MAPTYPE = new TypeToken<Map<String, Map<String, Double>>>() {
    }.getType();

    public ObligationJsonProcessor() {
        super(OBLIGATION_FILE_PATH, MAPTYPE);
    }

    @Override
    protected Map<String, Map<String, Double>> createDefaultData() {
        return new HashMap<>();
    }
}
