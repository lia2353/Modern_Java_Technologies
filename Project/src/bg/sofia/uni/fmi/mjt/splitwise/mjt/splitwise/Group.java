package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Group {

    private final String name;
    private final String uniqueName;
    private final Map<String, Double> membersExpenses;
    private final String admin;

    private static final String DELIMITER = ", ";
    private static final String DASH = "-";

    public Group(String name, String admin, Set<String> members) {
        this.name = name;
        this.uniqueName = name + DASH + admin;
        this.membersExpenses = members.stream()
                .collect(Collectors.toMap(item -> item, item -> 0.0));
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public Map<String, Double> getAllMembersExpenses() {
        return membersExpenses;
    }

    public boolean isAdmin(String username) {
        return username.equals(admin);
    }

    public Set<String> getMembers() {
        return membersExpenses.keySet();
    }

    public int getMembersCount() {
        return membersExpenses.size() + 1;
    }

    public boolean hasMember(String member) {
        return membersExpenses.containsKey(member);
    }

    public Double getMemberExpense(String member) {
        return membersExpenses.get(member);
    }

    public void setMemberExpense(String member, Double expense) {
        membersExpenses.put(member, expense);
    }

    public void addSplitExpense(String member, Double amount) {
        Double expense = membersExpenses.get(member);
        if (expense == null) {
            expense = 0.0;
        }
        expense -= amount;
        membersExpenses.put(member, expense);
    }

    public boolean allExpensesSettledUp() {
        return membersExpenses.values().stream().allMatch(item -> item.equals(0.0));
    }

    @Override
    public String toString() {
        return name + DELIMITER + membersExpenses.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Group group)) {
            return false;
        }
        return name.equals(group.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, membersExpenses);
    }
}
