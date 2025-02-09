package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.model.Notification;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationJsonProcessor extends AbstractJsonProcessor<Map<String, List<Notification>>> {
    private static final String NOTIFICATIONS_JSON_FILE_PATH = "resources/notifications.json";
    private static final Type NOTIFICATION_MAP_TYPE = new TypeToken<Map<String, List<Notification>>>() {
    }.getType();

    public NotificationJsonProcessor() {
        super(NOTIFICATIONS_JSON_FILE_PATH, NOTIFICATION_MAP_TYPE);
    }

    @Override
    protected Map<String, List<Notification>> createDefaultData() {
        return new HashMap<>();
    }
}