package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise;

import java.util.Objects;

public class Friend {

    private final String username;
    private Double expense;
    private static final String HAS = "=";

    public Friend(String username, Double expense) {
        this.username = username;
        this.expense = expense;
    }

    public String getUsername() {
        return username;
    }

    public Double getExpense() {
        return expense;
    }

    public void addSplitExpense(Double amount) {
        expense -= amount;
    }

    @Override
    public String toString() {
        return username + HAS + expense;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Friend friend)) {
            return false;
        }
        return username.equals(friend.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, expense);
    }
}
