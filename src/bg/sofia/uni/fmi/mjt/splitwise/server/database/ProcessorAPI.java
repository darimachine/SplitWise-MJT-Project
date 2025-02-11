package bg.sofia.uni.fmi.mjt.splitwise.server.database;

public interface ProcessorAPI<T> {

    T loadData();

    void saveData(T data);
}
