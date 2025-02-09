package bg.sofia.uni.fmi.mjt.splitwise.server.service.impl;

import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.UserJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.util.Map;

public class UserService implements UserServiceAPI {

    private final Map<String, User> users;
    private final UserJsonProcessor userJsonHandler;

    public UserService(UserJsonProcessor userJsonHandler) {
        this.userJsonHandler = userJsonHandler;
        this.users = userJsonHandler.loadData();
    }

    @Override
    public void addUser(User user) {
        users.put(user.getUsername(), user);
        userJsonHandler.saveData(users);
    }

    @Override
    public User getUser(String username) {
        return users.get(username);
    }

    @Override
    public void saveAll() {
        userJsonHandler.saveData(users);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getUsername(), user);
        saveAll();
    }
}
