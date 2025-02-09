package bg.sofia.uni.fmi.mjt.splitwise.server.database.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.LocalDateTime;

public class GsonSingleton {
    private GsonSingleton() {
    }

    private static class GsonHolder {
        private static final Gson INSTANCE = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()) // Register adapter
            .create();
    }

    public static Gson getInstance() {
        return GsonHolder.INSTANCE;
    }
}
