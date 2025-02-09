import bg.sofia.uni.fmi.mjt.splitwise.model.User;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.GroupJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.JsonProcessorFactory;
import bg.sofia.uni.fmi.mjt.splitwise.server.database.json.ObligationJsonProcessor;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.GroupServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.ObligationServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.UserServiceAPI;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.GroupService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.ObligationService;
import bg.sofia.uni.fmi.mjt.splitwise.server.service.impl.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        UserServiceAPI userManager = new UserService(JsonProcessorFactory.getUserJsonProcessor());

        User alex =
            new User("alex", "Aleks", "Familiq1", "hashed_password_123", Set.of("maria", "john"), Set.of("Roommates"));
        User maria =
            new User("maria", "Mariq", "Familiq2", "hashed_password_456", Set.of("alex"), Set.of("Roommates"));
        //Add users to the manager
//        userManager.addUser(alex);
//        userManager.addUser(maria);
        userManager.saveAll();
        // Access a user by username
        User fetchedUser = userManager.getUser("alex");
        System.out.println("Fetched User: " + fetchedUser);


//        NotificationServiceAPI notificationManager = new NotificationService(userManager);
//        Notification n1 = new Notification("Notifiction 1");
//        Notification n2 = new Notification("Notifiction 2");
//        notificationManager.addNotification("Notification 1", alex.getUsername());
//        notificationManager.addNotification("Notification 2", alex.getUsername());
//        notificationManager.addNotification("Notification 3", maria.getUsername());
//        notificationManager.getAllNotificationsForUser(alex.getUsername()).forEach(System.out::println);
//        Collection<Notification> userNotifications = notificationManager.getAllNotificationsForUser(alex.getUsername());
//        notificationManager.markNotificationsAsSeen(alex.getUsername(), userNotifications);
        ObligationServiceAPI obligationManager = new ObligationService(userManager, new ObligationJsonProcessor());
        obligationManager.addObligation("alex", "maria", 10);
        obligationManager.addObligation("maria", "alex", 5);
        Set<String> friends = alex.getFriends();
        System.out.println(obligationManager.getMyFriendsObligations("alex", friends));
        GroupServiceAPI groupManager = new GroupService(new GroupJsonProcessor());
        groupManager.createGroup("Roommates", Set.of("alex", "maria"));
        userManager.getUser("alex").addGroup("Roommates");
        userManager.getUser("maria").addGroup("Roommates");
        User user = userManager.getUser("alex");
        Set<String> userGroups = user.getGroups();
        Map<String, Set<String>> filteredGroups = new HashMap<>();
        for (String groupName : userGroups) {
            filteredGroups.put(groupName, groupManager.getGroupMembers(groupName));
        }
        System.out.println(obligationManager.getMyGroupObligations("alex", filteredGroups));

    }
}
