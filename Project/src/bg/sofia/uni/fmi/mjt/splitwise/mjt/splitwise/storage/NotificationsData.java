package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class NotificationsData {

    private static final String NO_NOTIFICATIONS_MESSAGE = "No notifications to show.";
    private static final String NOTIFICATIONS = "*** Notifications ***";
    private static final String FRIENDS = "Friends: ";
    private static final String GROUPS = "Groups: ";
    private static final String OWE_NOTIFICATION = "You owe {0} {1} LV [{2}].";
    private static final String APPROVED_PAYMENT_NOTIFICATION = "{0} approved your payment {1} LV.";

    private final Map<String, String> friendsNotifications;               //<user, friend-notifications-list>
    private final Map<String, Map<String, String>> groupsNotifications;   //<user, <group, group-notifications-list>>

    public NotificationsData() {
        this.friendsNotifications = new HashMap<>();
        this.groupsNotifications = new HashMap<>();
    }

    public void addPayment(String username, String owesWhom, Double amount, String reasonForPayment, String groupName) {
        if (username == null || owesWhom == null || amount == null || reasonForPayment == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        String notification = MessageFormat.format(OWE_NOTIFICATION, owesWhom, amount, reasonForPayment);
        if (groupName == null) {
            addFriendNotification(username, notification);
        } else {
            addGroupNotification(username, groupName, notification);
        }
    }

    public void addApprovedPayment(String username, String approvedBy, Double payment, String groupName) {
        if (username == null || approvedBy == null || payment == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        String notification = MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, approvedBy, payment);
        if (groupName == null) {
            addFriendNotification(username, notification);
        } else {
            addGroupNotification(username, groupName, notification);
        }
    }

    private void addFriendNotification(String username, String notification) {
        friendsNotifications.putIfAbsent(username, "");
        String old = friendsNotifications.get(username);
        friendsNotifications.put(username, old.concat(notification));
    }

    private void addGroupNotification(String username, String groupName, String notification) {
        groupsNotifications.computeIfAbsent(username, k -> new HashMap<>());
        groupsNotifications.get(username).putIfAbsent(groupName, "");
        String old = groupsNotifications.get(username).get(groupName);
        groupsNotifications.get(username).put(groupName, old.concat(notification));
    }

    public boolean hasNoNotifications(String username) {
        return !hasFriendsNotifications(username) && !hasGroupsNotifications(username);
    }

    public boolean hasFriendsNotifications(String username) {
        return friendsNotifications.get(username) != null;
    }

    public boolean hasGroupsNotifications(String username) {
        return groupsNotifications.get(username) != null;
    }

    private String getFriendsNotifications(String username) {
        String notifications = friendsNotifications.get(username);
        friendsNotifications.remove(username);
        return notifications;
    }

    private String getGroupsNotifications(String username) {
        StringBuilder notifications = new StringBuilder();
        for (Map.Entry<String, String> g : groupsNotifications.get(username).entrySet()) {
            notifications.append("* ").append(g.getKey()).append(System.lineSeparator()).append(g.getValue());
        }
        groupsNotifications.remove(username);
        return notifications.toString();
    }

    public String getNotifications(String username) {
        String notifications = NOTIFICATIONS;
        if (hasNoNotifications(username)) {
            notifications += System.lineSeparator() + NO_NOTIFICATIONS_MESSAGE;
        } else {
            if (hasFriendsNotifications(username)) {
                notifications += System.lineSeparator() + FRIENDS + System.lineSeparator()
                        + getFriendsNotifications(username);
            }
            if (hasGroupsNotifications(username)) {
                notifications += System.lineSeparator() + GROUPS + System.lineSeparator()
                        + getGroupsNotifications(username);
            }
        }
        return notifications;
    }

}
