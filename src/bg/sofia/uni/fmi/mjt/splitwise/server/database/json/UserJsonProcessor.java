package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class UserJsonProcessor extends AbstractJsonProcessor<Map<String, User>> {
    private static final String USERS_FILE_PATH = "resources/users.json";
    private static final Type USER_MAP_TYPE = new TypeToken<Map<String, User>>() {
    }.getType();

    public UserJsonProcessor() {
        super(USERS_FILE_PATH, USER_MAP_TYPE);
    }

    @Override
    protected Map<String, User> createDefaultData() {
        return new HashMap<>();
    }
}
