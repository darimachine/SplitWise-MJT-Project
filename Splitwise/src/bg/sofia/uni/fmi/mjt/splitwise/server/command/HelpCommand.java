package bg.sofia.uni.fmi.mjt.splitwise.server.command;

public class HelpCommand implements Command {

    @Override
    public String execute(String[] arguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("------List of all available commands: ------------------").append(System.lineSeparator())
            .append("--disconnect")
            .append(System.lineSeparator())
            .append("* Not authenticated: ")
            .append(System.lineSeparator())
            .append("-- login <username> <password> - logs in a user")
            .append(System.lineSeparator())
            .append("-- register <username> <password> <first_name> <last_name> - registers a new user")
            .append(System.lineSeparator())
            .append("* Authenticated: ").append(System.lineSeparator())
            .append("-- logout - logs out the current user")
            .append(System.lineSeparator())
            .append("-- add-friend <username> - adds a friend to the current user")
            .append(System.lineSeparator())
            .append("-- remove-friend <username> - removes a friend from the current user")
            .append(System.lineSeparator())
            .append("-- my-friends - lists all friends of the current user")
            .append(System.lineSeparator())
            .append("-- are-friends <user1> <user2> - checks if two users are friends")
            .append(System.lineSeparator());
        helperForMethodLength(sb);

        return sb.toString();
    }

    private void helperForMethodLength(StringBuilder sb) {
        sb.append("-- create-group <group_name> <user1> <user2> ... - creates a group with the given name and members")
            .append(System.lineSeparator())
            .append("-- my-groups - lists all groups of the current user").append(System.lineSeparator())
            .append("-- group-info <group_name> - shows the members of a group").append(System.lineSeparator())
            .append("-- add-friend-to-group <group_name> <username> - adds a friend to a group")
            .append(System.lineSeparator())
            .append("-- remove-friend-from-group <group_name> <username> - removes a friend from a group")
            .append(System.lineSeparator())
            .append("-- getUserGroups <username> - lists all groups of a user").append(System.lineSeparator())
            .append("-- my-expenses - lists all expenses of the current user").append(System.lineSeparator())
            .append("-- split <amount> <username> <reason> - splits an expense with a friend")
            .append(System.lineSeparator())
            .append("-- split-group <amount> <group_name> <reason> - splits an expense with a group")
            .append(System.lineSeparator())
            .append("-- get-status - shows the show status who owes you and to whom you owe")
            .append(System.lineSeparator())
            .append("-- payed <username> <amount> - marks a debt as payed")
            .append(System.lineSeparator())
            .append("-- all-notifications - lists all notifications of the current user") // tuka sme
            .append(System.lineSeparator())
            .append("-- notifications - lists only the unread notifications of the current user")
            .append(System.lineSeparator())
            .append("-- add-obligation <username> <amount> - adds an obligation which i owe to a friend")
            .append(System.lineSeparator())
            .append("-- find-obligation <username> - finds the obligation between me and a friend");
    }

}
