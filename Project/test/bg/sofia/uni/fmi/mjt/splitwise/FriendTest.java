package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.Friend;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FriendTest {

    private static final String USERNAME = "username";
    private static final Double ZERO_EXPENSE = 0.0;
    private static final Double EXPENSE = 10.5;
    private static final String HAS = "=";

    // getUsername
    @Test
    public void testGetUsername() {
        Friend friend = new Friend(USERNAME, EXPENSE);
        assertEquals("Unexpected friend name returned", USERNAME, friend.getUsername());
    }

    // getExpense
    @Test
    public void testGetExpense() {
        Friend friend = new Friend(USERNAME, EXPENSE);
        assertEquals("Unexpected friend expense returned", EXPENSE, friend.getExpense(), 0);
    }

    // addSplitExpense
    @Test
    public void testAddSplitExpense() {
        Friend friend = new Friend(USERNAME, ZERO_EXPENSE);

        assertEquals(ZERO_EXPENSE, friend.getExpense(), 0);
        friend.addSplitExpense(EXPENSE);
        assertEquals(-EXPENSE, friend.getExpense(), 0);
    }

    // toString
    @Test
    public void testToString() {
        Friend friend = new Friend(USERNAME, EXPENSE);

        String expected = USERNAME + HAS + EXPENSE;
        assertEquals("Unexpected string representation of friend returned", expected, friend.toString());
    }
}
