package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.Friend;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.Group;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ExpensesData {

    private final Map<String, Map<String, Friend>> friendExpenses;  //username <friendUsername, Friend>
    private final Map<String, Map<String, Group>> groupExpenses;    //username <groupName, Group>

    private static final String DELIMITER = ", ";
    private static final String DASH = "-";

    public ExpensesData() {
        friendExpenses = new HashMap<>();
        groupExpenses = new HashMap<>();
    }

    // Friends
    public boolean areFriends(String u1, String u2) {
        if (u1 == null || u2 == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        return friendExpenses.get(u1) != null
                && friendExpenses.get(u1).containsKey(u2);
    }

    public void addFriends(String u1, String u2) {
        if (u1 == null || u2 == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        //HERE: already friends case

        friendExpenses.computeIfAbsent(u1, k -> new HashMap<>());
        friendExpenses.get(u1).put(u2, new Friend(u2, 0.0));

        friendExpenses.computeIfAbsent(u2, k -> new HashMap<>());
        friendExpenses.get(u2).put(u1, new Friend(u1, 0.0));
    }

    public String getFriends(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        if (friendExpenses.get(username) == null) {
            return "";
        }
        return String.join(DELIMITER, friendExpenses.get(username).keySet());
    }

    public boolean isFriendOwing(String username, String friendName) {
        if (username == null || friendName == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        if (friendExpenses.get(username) == null || friendExpenses.get(username).get(friendName) == null) {
            return false;
        }
        return friendExpenses.get(username).get(friendName).getExpense() < 0.0;
    }

    public Double getFriendOwing(String username, String friendName) {
        if (username == null || friendName == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        // HERE: Not friends
        return Math.abs(friendExpenses.get(username).get(friendName).getExpense());
    }

    public void payedFriendExpense(String username, String friendName, Double payment) {
        if (username == null || friendName == null || payment == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        // HERE: Not friends
        Double expense = friendExpenses.get(username).get(friendName).getExpense();
        expense += payment;
        friendExpenses.get(username).put(friendName, new Friend(friendName, expense));
        friendExpenses.get(friendName).put(username, new Friend(username, Math.abs(expense)));
    }

    public Double splitFriendExpense(String username, String friendName, Double amount) {
        if (username == null || friendName == null || amount == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        Double splitAmount = roundTwoDigits(amount / 2);
        friendExpenses.get(username).get(friendName).addSplitExpense(splitAmount);
        friendExpenses.get(friendName).get(username).addSplitExpense(-splitAmount);
        return splitAmount;
    }

    public String getFriendsExpensesStatus(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        StringBuilder status = new StringBuilder();
        if (friendExpenses.get(username) != null) {
            for (Friend f : friendExpenses.get(username).values()) {
                Double expense = f.getExpense();
                if (expense.equals(0.0)) {
                    continue;
                }
                status.append(System.lineSeparator()).append(" * ").append(f.getUsername()).append(": ");
                status.append((expense < 0) ? "Owes you " + Math.abs(expense) : "You owe " + expense).append(" LV");
            }
        }
        return status.toString();
    }

    // Groups
    private String getUniqueGroupName(String username, String groupName) {
        return groupName.contains(DASH) ? groupName : groupName + DASH + username;
    }

    public boolean hasGroup(String username, String groupName) {
        if (username == null || groupName == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        if (groupExpenses.get(username) == null) {
            return false;
        }
        String uniqueGroupName = getUniqueGroupName(username, groupName);
        return groupExpenses.get(username).containsKey(groupName)
                || groupExpenses.get(username).containsKey(uniqueGroupName);
    }

    public void addGroup(String username, String admin, String groupName, Set<String> members) {
        if (username == null || admin == null || groupName == null || members == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        members = members.stream()
                .filter(u -> !u.equals(username))
                .collect(Collectors.toSet());

        groupExpenses.computeIfAbsent(username, k -> new HashMap<>());
        Group newGroup = new Group(groupName, admin, members);
        groupExpenses.get(username).put(newGroup.getUniqueName(), newGroup);
    }

    public String getGroups(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        if (groupExpenses.get(username) == null) {
            return "";
        }
        Set<String> groups = groupExpenses.get(username).values().stream()
                .map(g -> g.isAdmin(username) ? g.getName() : g.getUniqueName())
                .collect(Collectors.toSet());
        return String.join(DELIMITER, groups);
    }

    public Group getGroup(String username, String groupName) {
        if (groupExpenses.get(username).get(groupName) != null) {
            return groupExpenses.get(username).get(groupName);
        } else {
            return groupExpenses.get(username).get(getUniqueGroupName(username, groupName));
        }
    }

    public boolean hasGroupMember(String username, String groupName, String member) {
        String uniqueGroupName = getUniqueGroupName(username, groupName);
        return groupExpenses.get(username) != null
                && groupExpenses.get(username).get(uniqueGroupName).hasMember(member);
    }

    public boolean isMemberGroupOwing(String username, String groupName, String member) {
        String uniqueGroupName = getUniqueGroupName(username, groupName);
        return groupExpenses.get(username) != null
                && groupExpenses.get(username).get(uniqueGroupName).getMemberExpense(member) < 0.0;
    }

    public Double getMemberOwing(String username, String groupName, String member) {
        String uniqueGroupName = getUniqueGroupName(username, groupName);
        return Math.abs(groupExpenses.get(username).get(uniqueGroupName).getMemberExpense(member));
    }

    public void payedMemberExpense(String username, String groupName, String member, Double payment) {
        String uniqueGroupName = getUniqueGroupName(username, groupName);
        Double expense = groupExpenses.get(username).get(uniqueGroupName).getMemberExpense(member);
        expense += payment;
        groupExpenses.get(username).get(uniqueGroupName).setMemberExpense(member, expense);
        groupExpenses.get(member).get(uniqueGroupName).setMemberExpense(username, Math.abs(expense));
    }

    public Double splitGroupExpense(String username, String groupName, Double amount) {
        String uniqueGroupName = getUniqueGroupName(username, groupName);
        int membersCount = groupExpenses.get(username).get(uniqueGroupName).getMembersCount();
        Double splitAmount = roundTwoDigits(amount / membersCount);

        for (String member : groupExpenses.get(username).get(uniqueGroupName).getMembers()) {
            if (member.equals(username)) {
                groupExpenses.get(username).get(uniqueGroupName).addSplitExpense(member, -splitAmount);
            } else {
                groupExpenses.get(username).get(uniqueGroupName).addSplitExpense(member, splitAmount);
            }
            groupExpenses.get(member).get(uniqueGroupName).addSplitExpense(username, -splitAmount);
        }

        return splitAmount;
    }

    public String getGroupsExpensesStatus(String username) {
        StringBuilder status = new StringBuilder();
        if (groupExpenses.get(username) != null) {
            for (Group g : groupExpenses.get(username).values()) {
                if (!g.allExpensesSettledUp()) {
                    String groupName = g.isAdmin(username) ? g.getName() : g.getUniqueName();
                    status.append(System.lineSeparator()).append(" * ").append(groupName);
                    for (Map.Entry<String, Double> e : g.getAllMembersExpenses().entrySet()) {
                        Double expense = e.getValue();
                        if (expense.equals(0.0)) {
                            continue;
                        }
                        status.append(System.lineSeparator()).append(" - ").append(e.getKey()).append(": ");
                        status.append((expense < 0) ? "Owes you " + Math.abs(expense) : "You owe " + expense).append(" LV");
                    }
                }
            }
        }
        return status.toString();
    }

    // Helper methods
    private Double roundTwoDigits(Double amount) {
        return Math.ceil(amount * 100) / 100;
    }

    /*
    private static final String FRIENDS_EXPENSES_DATA_FILE_PATH = "./resources/friendsExpenses.txt";
    private static final String GROUPS_EXPENSES_DATA_FILE_PATH = "./resources/groupsExpenses.txt";

    private static final String HAS = "=";

    public void readFriendsExpensesData() {
        Path file = Path.of(FRIENDS_EXPENSES_DATA_FILE_PATH);
        if (Files.exists(file)) {
            try (BufferedReader br = Files.newBufferedReader(file)) {
                String line;
                while ((line = br.readLine()) != null) {
                    String username = line.split(DASH)[0];
                    Integer friendsCount = Integer.valueOf(line.split(DASH)[1]);
                    Map<String, Friend> friends = new HashMap<>();
                    for (int i = 0; i < friendsCount; ++i) {
                        line = br.readLine();
                        friends.put(line.split(HAS)[0], new Friend(line));
                    }
                    friendExpenses.put(username, friends);
                }
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while reading from the file", e);
            }
        }
    }

    public void writeFriendsExpensesData() {
        if (!friendExpenses.isEmpty()) {
            try (BufferedWriter fw = new BufferedWriter(new FileWriter(FRIENDS_EXPENSES_DATA_FILE_PATH, true))) {
                for (String username : friendExpenses.keySet()) {
                    int friendsCount = friendExpenses.get(username).size();
                    fw.write(username + DASH + friendsCount);
                    fw.flush();
                    for ( Friend friend : friendExpenses.get(username).values()) {
                        fw.write(friend.toString());
                        fw.flush();
                    }
                }
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while writing in the file", e);
            }
        }
    }
    */
}
