package bg.sofia.uni.fmi.mjt.splitwise.server.database.json;

import bg.sofia.uni.fmi.mjt.splitwise.server.database.ProcessorAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.gson.GsonSingleton;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractJsonProcessor<T> implements ProcessorAPI<T> {
    private final Path jsonFilePath;
    private final Gson gson = GsonSingleton.getInstance();
    private final Type typeToken;

    public AbstractJsonProcessor(String filePath, Type typeToken) {
        this.jsonFilePath = Path.of(filePath);
        this.typeToken = typeToken;
    }

    @Override
    public T loadData() {
        if (!Files.exists(jsonFilePath)) {
            return createDefaultData();
        }
        try (Reader reader = Files.newBufferedReader(jsonFilePath)) {
            T data = gson.fromJson(reader, typeToken);
            return data != null ? data : createDefaultData();
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while loading the data from the json file", e);
        }
    }

    @Override
    public void saveData(T data) {
        try (Writer write = Files.newBufferedWriter(jsonFilePath)) {
            gson.toJson(data, write);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while saving the data to the json file", e);
        }
    }

    protected abstract T createDefaultData(); // Method to return default empty data
}
