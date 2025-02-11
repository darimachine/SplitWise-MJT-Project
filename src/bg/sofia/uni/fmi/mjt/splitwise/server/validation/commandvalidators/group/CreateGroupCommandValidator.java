package bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.group;

import bg.sofia.uni.fmi.mjt.splitwise.exceptions.groups.CannotAddYourselfToGroupException;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.commandvalidators.AbstractCommandValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.GroupValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.validation.servicevalidators.UserValidator;
import bg.sofia.uni.fmi.mjt.splitwise.server.security.AuthenticationManager;

import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.CANNOT_ADD_SELF_TO_GROUP_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.INVALID_CREATE_GROUP_COMMAND_MESSAGE;
import static bg.sofia.uni.fmi.mjt.splitwise.server.helpers.Messages.NON_EMPTY_COMMANDS_MESSAGE;

public class CreateGroupCommandValidator extends AbstractCommandValidator {

    private static final int MIN_REQUIRED_ARGUMENTS = 4; // "create-group <group_name> <user1> <user2>"
    private final GroupValidator groupValidator;
    private final UserValidator userValidator;

    public CreateGroupCommandValidator(AuthenticationManager authManager, GroupValidator groupValidator,
                                       UserValidator userValidator) {
        super(authManager);
        this.groupValidator = groupValidator;
        this.userValidator = userValidator;
    }

    @Override
    public void validate(String[] args, SocketChannel clientChannel) {
        validateMinArguments(args, MIN_REQUIRED_ARGUMENTS, INVALID_CREATE_GROUP_COMMAND_MESSAGE);
        validateArgumentsNull(args, NON_EMPTY_COMMANDS_MESSAGE);
        validateAuthentication(clientChannel);

        String groupName = args[1];
        Set<String> members = Arrays.stream(args).skip(2).collect(Collectors.toSet());
        String loggedUser = authManager.getAuthenticatedUser(clientChannel).getUsername();

        groupValidator.validateGroupDoesNotExist(groupName);
        groupValidator.validateGroupSize(members);
        userValidator.validateUsersExist(members);
        if (members.contains(loggedUser)) {
            throw new CannotAddYourselfToGroupException(CANNOT_ADD_SELF_TO_GROUP_MESSAGE);
        }

    }
}