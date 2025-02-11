package bg.sofia.uni.fmi.mjt.splitwise.server.command.authentication;

import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.CommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.PasswordHasher;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;

import java.nio.channels.SocketChannel;

public class RegisterCommand implements Command {

    private final UserServiceAPI userService;
    private final CommandValidator validator;
    private static final int USERNAME_INDEX = 1;
    private static final int PASS_INDEX = 2;
    private static final int FIRST_NAME_INDEX = 3;
    private static final int LAST_NAME_INDEX = 4;

    public RegisterCommand(UserServiceAPI userService, CommandValidator validator) {
        this.userService = userService;
        this.validator = validator;
    }

    @Override
    public String execute(String[] arguments, SocketChannel clientChannel) {
        validator.validate(arguments, clientChannel);
        String username = arguments[USERNAME_INDEX];
        String password = arguments[PASS_INDEX];
        String firstName = arguments[FIRST_NAME_INDEX];
        String lastName = arguments[LAST_NAME_INDEX];
        User user = new User(username, PasswordHasher.hashPassword(password), firstName, lastName);
        userService.addUser(user);
        return "Successfully created user " + username + "!"
            + System.lineSeparator()
            + "Please register or login to get started.";
    }
}
