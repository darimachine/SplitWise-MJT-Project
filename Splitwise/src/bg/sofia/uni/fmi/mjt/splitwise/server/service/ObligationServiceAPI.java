package bg.sofia.uni.fmi.mjt.splitwise.server.service;

import java.util.Map;
import java.util.Set;

public interface ObligationServiceAPI {

    Map<String, Map<String, Double>> getAllObligations();

    double findObligationBetweenUsers(String fromuser, String toUser);

    Map<String, Double> getAllObligationsForUser(String username);

    //split
    void addObligation(String fromUser, String toUser, double amount);

    void payObligation(String fromUser, String toUser, double amount);

    String getMyFriendsObligations(String username, Set<String> friends);

    String getMyGroupObligations(String loggedUsername, Map<String, Set<String>> groups);
}
