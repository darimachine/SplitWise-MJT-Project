package bg.sofia.uni.fmi.mjt.splitwise.server.helpers;

public class Messages {
    //Welcome Messages
    public static final String PLEASE_LOGIN_OR_REGISTER_MESSAGE =
        "Please register or login to get started.";
    public static final String NOT_AUTHENTICATED_MESSAGE =
        """
            You don't have access to this command!
            You are not authenticated.
            You must be logged in to perform this action.
            """;
    public static final String CLIENT_WELCOME_MESSAGE =
        """
            ---------Welcome to Splitwise!----------
            Type help to see available commands.
            ---------------------------------------
            """;
    public static final String NON_EMPTY_COMMANDS_MESSAGE = "All fields must be provided and non-empty ";
    public static final String INVALID_COMMAND_MESSAGE = "Invalid command! Cannot recognize command ";
    public static final String EMPTY_COMMAND_MESSAGE = "Empty command! Command cannot be empty ";
    public static final String UNEXPECTED_ERROR_MESSAGE = "Unexpected Error occured. Please try again. ";
    //Register
    public static final String INVALID_REGISTER_COMMAND_MESSAGE =
        "Invalid register command! Usage: register <username> <password> <first_name> <last_name> ";
    public static final String NON_EMPTY_REGISTER_FIELDS_MESSAGE =
        "All fields (username, password, first name, last name) must be provided and non-empty. ";
    public static final String INVALID_PASSWORD_MESSAGE =
        "Password must be at least 8 characters long, contain at least one digit, "
            + "one uppercase letter and one lowercase letter.";
    //Login
    public static final String INVALID_LOGIN_COMMAND_MESSAGE =
        "Invalid login command! Usage: login <username> <password>";

    public static final String NON_EMPTY_LOGIN_FIELDS_MESSAGE =
        "Login fields cannot be empty!";
    //LogOut
    public static final String INVALID_LOGOUT_COMMAND_MESSAGE =
        "Invalid logout command! Usage: logout";

    //AddFriend
    public static final String INVALID_ADD_FRIEND_COMMAND_MESSAGE =
        "Invalid add-friend command! Usage: add-friend <username>";

    public static final String CANNOT_ADD_SELF_AS_FRIEND_MESSAGE =
        "You cannot add yourself as a friend!";
    //RemoveFriend
    public static final String INVALID_REMOVE_FRIEND_COMMAND_MESSAGE =
        "Invalid remove-friend command! Usage: remove-friend <username>";

    public static final String CANNOT_REMOVE_SELF_AS_FRIEND_MESSAGE =
        "You cannot remove yourself as a friend!";
    //my-friends
    public static final String INVALID_SHOW_FRIENDS_COMMAND_MESSAGE =
        "Invalid my-friends command! Usage: my-friends";
    //AreFriends
    public static final String INVALID_ARE_FRIENDS_COMMAND_MESSAGE =
        "Invalid are-friends command! Usage: are-friends <user1> <user2>";
    //CreateGroup
    public static final String INVALID_CREATE_GROUP_COMMAND_MESSAGE =
        "Invalid create-group command! Usage: create-group <group_name> <user1> <user2> ...";
    public static final String CANNOT_ADD_SELF_TO_GROUP_MESSAGE =
        "You cannot add yourself to the group!";

    public static final String GROUP_ALREADY_EXISTS_MESSAGE =
        "A group with this name already exists. Please choose a different name.";
    // "my-groups" Command Errors
    public static final String INVALID_MY_GROUPS_COMMAND_MESSAGE =
        "Invalid usage of 'my-groups'. No additional arguments are needed.";
    public static final String USER_NOT_IN_ANY_GROUP_MESSAGE = "You are not part of any groups.";
    // "group-info" Command Errors
    public static final String INVALID_GROUP_INFO_COMMAND_MESSAGE =
        "Invalid usage of 'group-info'. Correct usage: group-info <group_name>";
    public static final String GROUP_NOT_FOUND_MESSAGE = "The specified group does not exist.";
    public static final String USER_NOT_IN_GROUP_MESSAGE = "You are not a member of this group.";
    // "add-friend-to-group" Command Errors
    public static final String INVALID_ADD_FRIEND_TO_GROUP_COMMAND_MESSAGE =
        "Invalid usage of 'add-friend-to-group'. Correct usage: add-friend-to-group <group_name> <friend_username>";
    public static final String FRIEND_ALREADY_IN_GROUP_MESSAGE = "This friend is already a member of the group.";
    public static final String FRIEND_NOT_FOUND_MESSAGE = "The specified friend does not exist.";
    // "remove-friend-from-group" Command Errors
    public static final String INVALID_REMOVE_FRIEND_FROM_GROUP_COMMAND_MESSAGE =
        "Invalid usage of 'remove-friend-from-group'. " +
            "Correct usage: remove-friend-from-group <group_name> <friend_username>";
    public static final String FRIEND_NOT_IN_GROUP_MESSAGE = "This friend is not a member of the group.";
    public static final String CANNOT_REMOVE_SELF_MESSAGE =
        "You cannot remove yourself from the group using this command.";
    //Show User in Which Groups is
    public static final String INVALID_GET_USER_GROUPS_COMMAND_MESSAGE =
        "Invalid command format! Usage: getUserGroups <username>. You must provide a valid username.";
    //My Expense
    public static final String INVALID_MY_EXPENSES_COMMAND_MESSAGE =
        "Invalid command format! Usage: my-expenses. This command does not take additional arguments.";
    //Split with Friend
    public static final String INVALID_SPLIT_COMMAND_MESSAGE =
        "Invalid command format! Usage: split <amount> <username> <reason>.";
    public static final String INVALID_AMOUNT_MESSAGE =
        "Invalid amount! The amount must be a positive number.";
    //Split with Group
    public static final String INVALID_SPLIT_GROUP_COMMAND_MESSAGE =
        "Invalid command format! Usage: split-group <amount> <group_name> <reason>.";
    //Approve Payment:
    public static final String INVALID_APPROVE_PAYMENT_COMMAND_MESSAGE =
        "Invalid command! Usage: payed <username> <amount>";
    public static final String INVALID_PAYMENT_AMOUNT_MESSAGE =
        "Invalid amount! Payment amount must be a positive number.";
    //Notifications
    public static final String INVALID_SHOW_ALL_NOTIFICATIONS_COMMAND_MESSAGE =
        "Invalid command! Usage: all-notifications - lists all notifications of the current user.";
    public static final String INVALID_SHOW_NEW_NOTIFICATIONS_COMMAND_MESSAGE =
        "Invalid command! Usage: notifications - lists only the unread notifications of the current user.";
    public static final String INVALID_ADD_OBLIGATION_COMMAND_MESSAGE =
        "Invalid command! Usage: add-obligation <username> <amount> - adds an obligation which you owe to a friend.";
    public static final String INVALID_FIND_OBLIGATION_COMMAND_MESSAGE =
        "Invalid command! Usage: find-obligation <username> - finds the obligation between you and a friend.";
}
