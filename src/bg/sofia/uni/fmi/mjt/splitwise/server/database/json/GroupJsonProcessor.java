package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GroupJsonProcessor extends AbstractJsonProcessor<Map<String, Set<String>>> {
    private static final String GROUPS_FILE_PATH = "resources/groups.json";
    private static final Type GROUPS_TYPE = new TypeToken<Map<String, Set<String>>>() {
    }.getType();

    public GroupJsonProcessor() {
        super(GROUPS_FILE_PATH, GROUPS_TYPE);
    }

    @Override
    protected Map<String, Set<String>> createDefaultData() {
        return new HashMap<>();
    }
}
