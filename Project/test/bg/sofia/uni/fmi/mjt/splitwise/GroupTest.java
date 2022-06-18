package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.Group;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class GroupTest {

    private static final String DASH = "-";
    private static final String GROUP_NAME = "GroupName";
    private static final String ADMIN = "admin";
    private static final String NOT_ADMIN = "notAdmin";
    private static final String UNIQUE_NAME = GROUP_NAME + DASH + ADMIN;
    private final Set<String> members = Set.of("member", "member1", "member2", "member3");
    private final List<Double> expenses = members.stream().map(m -> 0.0).collect(Collectors.toList());
    private static final String MEMBER = "member";
    private static final String NOT_MEMBER = "notMember";
    private static final Double AMOUNT = 10.5;
    private static final String DELIMITER = ", ";

    // getName
    @Test
    public void testGetName() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertEquals("Unexpected group name returned", GROUP_NAME, group.getName());
    }

    // getUniqueName
    @Test
    public void testGetUniqueName() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertEquals("Unexpected unique group name returned", UNIQUE_NAME, group.getUniqueName());
    }

    // getAllMembersExpenses
    @Test
    public void testGetAllMembersExpensesWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertTrue("Should return empty members list", group.getAllMembersExpenses().isEmpty());
    }

    @Test
    public void testGetAllMembersExpensesWithListOfMembers() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        Map<String, Double> output = group.getAllMembersExpenses();

        assertEquals(members.size(), output.size());
        assertTrue(output.keySet().containsAll(members));
        assertTrue(output.values().containsAll(expenses));
        assertTrue(members.containsAll(output.keySet()));
        assertTrue(expenses.containsAll(output.values()));
    }

    // isAdmin
    @Test
    public void testIsAdminWithAdmin() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertTrue("Unexpected not admin returned", group.isAdmin(ADMIN));
    }

    @Test
    public void testIsAdminWithNotAdmin() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertFalse("Unexpected is admin returned", group.isAdmin(NOT_ADMIN));
    }

    // getMembers
    @Test
    public void testGetMembersWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertTrue("Should return empty members list", group.getMembers().isEmpty());
    }

    @Test
    public void testGetMembersWithListOfMembers() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        Set<String> output = group.getMembers();

        assertEquals(members.size(), output.size());
        assertTrue(output.containsAll(members));
        assertTrue(members.containsAll(output));
    }

    // getMembersCount
    @Test
    public void testGetMembersCountWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertEquals("Should return members list with 1 elements for admin", 1, group.getMembersCount());
    }

    @Test
    public void testGetMembersCountWithListOfMembers() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        assertEquals(members.size() + 1, group.getMembersCount());
    }

    // hasMember
    @Test
    public void testHasMemberWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertFalse("Should return false for no members to find", group.hasMember(MEMBER));
    }

    @Test
    public void testHasMemberWithGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        assertTrue(group.hasMember(MEMBER));
    }

    @Test
    public void testHasMemberWithoutGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        assertFalse(group.hasMember(NOT_MEMBER));
    }

    // getMemberExpense
    @Test
    public void testGetMemberExpenseWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertNull(group.getMemberExpense(MEMBER));
    }

    @Test
    public void testGetMemberExpenseWithGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        assertEquals(0.0, group.getMemberExpense(MEMBER), 0);
    }

    @Test
    public void testGetMemberExpenseWithoutGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        assertNull(group.getMemberExpense(NOT_MEMBER));
    }

    // setMemberExpense
    @Test
    public void testSetMemberExpenseWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());

        assertNull(group.getMemberExpense(MEMBER));
        group.setMemberExpense(MEMBER, AMOUNT);
        assertEquals(AMOUNT, group.getMemberExpense(MEMBER), 0);
    }

    @Test
    public void testSetMemberExpenseWithGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);

        assertEquals(0.0, group.getMemberExpense(MEMBER), 0);
        group.setMemberExpense(MEMBER, AMOUNT);
        assertEquals(AMOUNT, group.getMemberExpense(MEMBER), 0);
    }

    @Test
    public void testSetMemberExpenseWithoutGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);

        assertNull(group.getMemberExpense(NOT_MEMBER));
        group.setMemberExpense(MEMBER, AMOUNT);
        assertEquals(AMOUNT, group.getMemberExpense(MEMBER), 0);
    }

    // addSplitExpense
    @Test
    public void testAddSplitExpenseWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());

        assertNull(group.getMemberExpense(MEMBER));
        group.addSplitExpense(MEMBER, AMOUNT);
        assertEquals(-AMOUNT, group.getMemberExpense(MEMBER), 0);
    }

    @Test
    public void testAddSplitExpenseWithGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);

        assertEquals(0.0, group.getMemberExpense(MEMBER), 0);
        group.addSplitExpense(MEMBER, AMOUNT);
        assertEquals(-AMOUNT, group.getMemberExpense(MEMBER), 0);
    }

    @Test
    public void testAddSplitExpenseWithoutGivenMember() {
        Group group = new Group(GROUP_NAME, ADMIN, members);

        assertNull(group.getMemberExpense(NOT_MEMBER));
        group.addSplitExpense(MEMBER, AMOUNT);
        assertEquals(-AMOUNT, group.getMemberExpense(MEMBER), 0);
    }

    // allExpensesSettledUp
    @Test
    public void testAllExpensesSettledUpWithEmptyMembersList() {
        Group group = new Group(GROUP_NAME, ADMIN, new HashSet<>());
        assertTrue(group.allExpensesSettledUp());
    }

    @Test
    public void testAllExpensesSettledUpWithNoExpenses() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        assertTrue(group.allExpensesSettledUp());
    }

    @Test
    public void testAllExpensesSettledUpWithExpenses() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        group.setMemberExpense(MEMBER, AMOUNT);
        assertFalse(group.allExpensesSettledUp());
    }

    // toString
    @Test
    public void testToString() {
        Group group = new Group(GROUP_NAME, ADMIN, members);
        Map<String, Double> memberExpenses = members.stream().collect(Collectors.toMap(item -> item, item -> 0.0));

        String expected = GROUP_NAME + DELIMITER + memberExpenses.entrySet();
        assertEquals("Unexpected string representation of group returned", expected, group.toString());
    }

}
