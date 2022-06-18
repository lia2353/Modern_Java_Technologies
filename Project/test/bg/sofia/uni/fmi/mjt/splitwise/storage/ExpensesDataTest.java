package bg.sofia.uni.fmi.mjt.splitwise.storage;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import org.junit.Before;
import org.junit.Test;


import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExpensesDataTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final Double TEST_AMOUNT = 4.2;
    private static final String TEST_GROUP_NAME = "testGroupName";
    private static final String TEST_GROUP_NAME2 = "testGroupName2";
    private static final String TEST_FRIEND_NAME = "testFriendName";
    private static final String TEST_FRIEND_NAME2 = "testFriendName2";
    private static final String TEST_MEMBER = "testMember";
    private static final String TEST_MEMBER2 = "testMember2";

    private static final String DELIMITER = ", ";

    ExpensesData expenses;

    @Before
    public void setUp() {
        expenses = new ExpensesData();
    }

    // areFriend
    @Test(expected = IllegalArgumentException.class)
    public void testAreFriendsNullArguments() {
        expenses.areFriends(null, null);
    }

    @Test
    public void testAreFriendsWithNoFriends() {
        assertFalse(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME));
    }

    @Test
    public void testAreFriendsWithFriends() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertTrue(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME));
        assertTrue(expenses.areFriends(TEST_FRIEND_NAME, TEST_USERNAME));
    }

    // addFriends
    @Test(expected = IllegalArgumentException.class)
    public void testAddFriendsNullArguments() {
        expenses.addFriends(null, null);
    }

    @Test
    public void testAddFriendsWithValidUsers() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertTrue(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME));
        assertEquals(0.0, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);
        assertEquals(0.0, expenses.getFriendOwing(TEST_FRIEND_NAME, TEST_USERNAME), 0);
    }

    // getFriends
    @Test(expected = IllegalArgumentException.class)
    public void testGetFriendsNullArguments() {
        expenses.getFriends(null);
    }

    @Test
    public void testGetFriendsWithNoFriends() {
        assertEquals("", expenses.getFriends(TEST_USERNAME));
    }

    @Test
    public void testGetFriendsWithFriends() {
        String expected = TEST_FRIEND_NAME + DELIMITER + TEST_FRIEND_NAME2;

        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME2);
        assertEquals(expected, expenses.getFriends(TEST_USERNAME));
    }

    // isFriendOwing
    @Test(expected = IllegalArgumentException.class)
    public void testIsFriendsOwingNullArguments() {
        expenses.isFriendOwing(null, null);
    }

    @Test
    public void testIsFriendsOwingWithNotFriends() {
        assertFalse(expenses.isFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME));
    }

    @Test
    public void testIsFriendsOwingWithFriendsNoOwing() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertFalse(expenses.isFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME));
    }

    @Test
    public void testIsFriendsOwingWithFriendsIsOwing() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        expenses.splitFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        assertTrue(expenses.isFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME));
    }

    // getFriendOwing
    @Test(expected = IllegalArgumentException.class)
    public void testGetFriendsOwingNullArguments() {
        expenses.getFriendOwing(null, null);
    }

    @Test
    public void testGetFriendsOwingWithFriendsNoOwing() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertEquals(0.0, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);
    }

    @Test
    public void testGetFriendsOwingWithFriendsIsOwing() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        expenses.splitFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        assertEquals(TEST_AMOUNT / 2, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);
    }

    // payedFriendExpense
    @Test(expected = IllegalArgumentException.class)
    public void testPayedFriendExpenseNullArguments() {
        expenses.payedFriendExpense(null, null, null);
    }

    @Test
    public void testPayedFriendExpenseWithFriends() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertEquals(0.0, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);

        expenses.payedFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        assertEquals(TEST_AMOUNT, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);
        assertEquals(TEST_AMOUNT, expenses.getFriendOwing(TEST_FRIEND_NAME, TEST_USERNAME), 0);
    }

    // splitFriendExpense
    @Test(expected = IllegalArgumentException.class)
    public void testSplitFriendExpenseNullArguments() {
        expenses.splitFriendExpense(null, null, null);
    }

    @Test
    public void testSplitFriendExpenseWithFriends() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertEquals(0.0, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);

        expenses.payedFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        assertEquals(TEST_AMOUNT, expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME), 0);
        assertEquals(TEST_AMOUNT, expenses.getFriendOwing(TEST_FRIEND_NAME, TEST_USERNAME), 0);
    }

    // getFriendsExpensesStatus
    @Test(expected = IllegalArgumentException.class)
    public void testGetFriendsExpensesStatusNullArguments() {
        expenses.getFriendsExpensesStatus(null);
    }

    @Test
    public void testGetFriendsExpensesStatusWithFriendsNoOwing() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        assertEquals("", expenses.getFriendsExpensesStatus(TEST_USERNAME));
    }

    @Test
    public void testGetFriendsExpensesStatusWithFriendsIsOwing() {
        expenses.addFriends(TEST_USERNAME, TEST_FRIEND_NAME);
        expenses.splitFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        String expectedUserStatus = System.lineSeparator() + " * " + TEST_FRIEND_NAME + ": " + "Owes you "
                + TEST_AMOUNT / 2 + " LV";
        String expectedFriendStatus = System.lineSeparator() + " * " + TEST_USERNAME + ": " + "You owe "
                + TEST_AMOUNT / 2 + " LV";

        assertEquals(expectedUserStatus, expenses.getFriendsExpensesStatus(TEST_USERNAME));
        assertEquals(expectedFriendStatus, expenses.getFriendsExpensesStatus(TEST_FRIEND_NAME));
    }

    // hasGroup
    @Test(expected = IllegalArgumentException.class)
    public void testHasGroupNullArguments() {
        expenses.hasGroup(null, null);
    }

    @Test
    public void testHasGroupWithNoGroups() {
        assertFalse(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME));
    }

    @Test
    public void testHasGroupWithNoSuchGroup() {
        expenses.addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME, Set.of(TEST_MEMBER, TEST_MEMBER2));
        assertFalse(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME2));
    }

    @Test
    public void testHasGroupWithGivenGroup() {
        expenses.addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME, Set.of(TEST_MEMBER, TEST_MEMBER2));
        assertTrue(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME));
    }

    // addGroup
    @Test(expected = IllegalArgumentException.class)
    public void testAddGroupNullArguments() {
        expenses.addGroup(null, null, null, null);
    }

    @Test
    public void testAddGroupWithGivenGroup() {
        expenses.addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME, Set.of(TEST_MEMBER, TEST_MEMBER2));
        assertTrue(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME));
        assertFalse(expenses.hasGroup(TEST_MEMBER, TEST_GROUP_NAME));
        assertEquals(TEST_GROUP_NAME, expenses.getGroups(TEST_USERNAME));
    }

    // getGroups
    @Test(expected = IllegalArgumentException.class)
    public void testGetGroupsNullArguments() {
        expenses.getGroups(null);
    }

    @Test
    public void testGetGroupsWithNoGroups() {
        assertEquals("", expenses.getGroups(TEST_USERNAME));
    }

    @Test
    public void testGetGroupsWithGroups() {
        expenses.addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME, Set.of(TEST_MEMBER, TEST_MEMBER2));
        expenses.addGroup(TEST_USERNAME, TEST_USERNAME, TEST_GROUP_NAME2, Set.of(TEST_MEMBER, TEST_MEMBER2));

        String groups = expenses.getGroups(TEST_USERNAME);
        assertTrue(groups.contains(TEST_GROUP_NAME));
        assertTrue(groups.contains(TEST_GROUP_NAME2));
        assertEquals(DELIMITER, groups.replace(TEST_GROUP_NAME2, "").replace(TEST_GROUP_NAME, ""));
    }

}
