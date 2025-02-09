package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;

public interface UserServiceAPI {

    User getUser(String username);

    void addUser(User user);

    void updateUser(User user);

    void saveAll();
}
