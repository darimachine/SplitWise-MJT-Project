package bg.sofia.uni.fmi.mjt.splitwise.model;

import bg.sofia.uni.fmi.mjt.splitwise.model.enums.Currency;

import java.util.Set;

public class User {


    private final String username;
    private final String password;
    private final String firstName;
    private final String lastName;
    private final Set<String> friends;
    private final Set<String> groups;
    private Currency preferredCurrency;

    public User(String username, String password, String firstName, String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.friends = Set.of();
        this.groups = Set.of();
        this.preferredCurrency = Currency.BGN;
    }

    public User(String username, String firstName, String lastName, String password, Set<String> friends,
                Set<String> groups) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.friends = friends;
        this.groups = groups;
        this.preferredCurrency = Currency.BGN;
    }

    public void addFriend(String friend) {
        friends.add(friend);
    }

    public void removeFriend(String friend) {
        friends.remove(friend);
    }

    public void addGroup(String group) {
        groups.add(group);
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPassword() {
        return password;
    }

    public Set<String> getFriends() {
        return friends;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public Currency getPreferredCurrency() {
        return preferredCurrency;
    }

    public void setCurrency(Currency currency) {
        this.preferredCurrency = currency;
    }
}
