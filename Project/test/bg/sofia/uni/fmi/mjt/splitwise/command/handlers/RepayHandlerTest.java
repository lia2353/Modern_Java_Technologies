package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.RepayHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepayHandlerTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_FRIEND_NAME = "testFriendName";
    private static final String TEST_GROUP_NAME = "testGroupName";
    private static final String TEST_MEMBER = "testMember";
    private static final Double TEST_AMOUNT = 4.2;
    private static final Double TEST_AMOUNT_INVALID = -4.2;

    // Commands
    private static final String REPAYED_COMMAND = "repayed";
    private static final String REPAYED_COMMAND_INVALID_ARGUMENTS_COUNT = REPAYED_COMMAND;
    private static final String REPAYED_COMMAND_INVALID_AMOUNT_TYPE
            = REPAYED_COMMAND + " " + TEST_AMOUNT_INVALID + " " + TEST_FRIEND_NAME;
    private static final String REPAYED_COMMAND_VALID = REPAYED_COMMAND + " " + TEST_AMOUNT + " " + TEST_FRIEND_NAME;
    private static final String REPAYED_FOR_GROUP_COMMAND = "repayed-group";
    private static final String REPAYED_FOR_GROUP_COMMAND_INVALID_ARGUMENTS_COUNT = REPAYED_FOR_GROUP_COMMAND;
    private static final String REPAYED_FOR_GROUP_COMMAND_INVALID_AMOUNT_TYPE
            = REPAYED_FOR_GROUP_COMMAND + " " + TEST_AMOUNT_INVALID + " " + TEST_GROUP_NAME + " " + TEST_MEMBER;
    private static final String REPAYED_FOR_GROUP_COMMAND_VALID
            = REPAYED_FOR_GROUP_COMMAND + " " + TEST_AMOUNT + " " + TEST_GROUP_NAME + " " + TEST_MEMBER;

    // Messages
    private static final String SUCCESSFUL_PAYMENT_MESSAGE = "{0} payed you {1} LV." + System.lineSeparator()
            + "Current status: Owes you {2} LV";
    private static final String SUCCESSFUL_GROUP_PAYMENT_MESSAGE = "{0} payed you {1} LV for {2} group."
            + System.lineSeparator() + "Current status: Owes you {3} LV for group {2}";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String NO_SUCH_FRIEND_MESSAGE = "You are not friends with {0}.";
    private static final String NO_USER_EXPENSE_MESSAGE = "There are no expenses for friend {0}.";
    private static final String FRIEND_OWES_LESS_MESSAGE = "Invalid operation. {0} owes you only {1} LV";
    private static final String NO_SUCH_GROUP_MESSAGE = "You have no group named {0}.";
    private static final String NO_SUCH_GROUP_MEMBER_MESSAGE = "{0} group has no member {1}.";
    private static final String NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE
            = "Provide valid number for amount argument. Refer to help command for more information.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int TWO_ARGUMENTS = 2;

    private static LoginData loginData;
    private static ExpensesData expenses;
    private static NotificationsData notifications;

    private static SocketChannel testSocket;
    private Command cmd;

    private static RepayHandler repayHandler;

    @Before
    public void setUp() {
        loginData = mock(LoginData.class);
        expenses = mock(ExpensesData.class);
        notifications = mock(NotificationsData.class);
        testSocket = mock(SocketChannel.class);
        repayHandler = new RepayHandler(loginData, expenses, notifications);
    }

    // REPAYED_COMMAND
    @Test
    public void testgetRepayedCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REPAYED_COMMAND, TWO_ARGUMENTS);
        cmd = new Command(REPAYED_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testgetRepayedCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(REPAYED_COMMAND_VALID);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testgetRepayedCommandInvalidAmountType() {
        when(loginData.isLogged(testSocket)).thenReturn(true);

        String expected = NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        cmd = new Command(REPAYED_COMMAND_INVALID_AMOUNT_TYPE);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testgetRepayedCommandNotFriends() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_FRIEND_MESSAGE, TEST_FRIEND_NAME);
        cmd = new Command(REPAYED_COMMAND_VALID);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testgetRepayedCommandNoOwing() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);
        when(expenses.isFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(false);

        String expected = MessageFormat.format(NO_USER_EXPENSE_MESSAGE, TEST_FRIEND_NAME);
        cmd = new Command(REPAYED_COMMAND_VALID);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testgetRepayedCommandOwesLess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);
        when(expenses.isFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);
        when(expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(TEST_AMOUNT - 1);

        String expected = MessageFormat.format(FRIEND_OWES_LESS_MESSAGE, TEST_FRIEND_NAME, TEST_AMOUNT - 1);
        cmd = new Command(REPAYED_COMMAND_VALID);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testgetRepayedCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);
        when(expenses.isFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);
        when(expenses.getFriendOwing(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(TEST_AMOUNT + 1);
        doNothing().when(expenses).payedFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        when(loginData.isUsernameLogged(TEST_FRIEND_NAME)).thenReturn(false);
        doNothing().when(notifications).addApprovedPayment(TEST_FRIEND_NAME, TEST_USERNAME, TEST_AMOUNT, null);

        String expected = MessageFormat.format(SUCCESSFUL_PAYMENT_MESSAGE, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_AMOUNT + 1);
        cmd = new Command(REPAYED_COMMAND_VALID);
        String actual = repayHandler.getRepayed(testSocket, cmd);

        assertEquals(expected, actual);
        verify(expenses, times(1)).payedFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT);
        verify(notifications, times(1)).addApprovedPayment(TEST_FRIEND_NAME, TEST_USERNAME, TEST_AMOUNT, null);
    }

    // REPAYED_FOR_GROUP_COMMAND
    @Test
    public void testGetRepayedForGroupCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REPAYED_FOR_GROUP_COMMAND, TWO_ARGUMENTS);
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRepayedForGroupCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_VALID);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRepayedForGroupCommandInvalidAmountType() {
        when(loginData.isLogged(testSocket)).thenReturn(true);

        String expected = NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_INVALID_AMOUNT_TYPE);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRepayedForGroupCommandNoSuchGroup() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_GROUP_MESSAGE, TEST_GROUP_NAME);
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_VALID);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRepayedForGroupCommandNoSuchMemberInGroup() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(true);
        when(expenses.hasGroupMember(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_GROUP_MEMBER_MESSAGE, TEST_GROUP_NAME, TEST_MEMBER);
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_VALID);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRepayedForGroupCommandhNoMemberOwing() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(true);
        when(expenses.hasGroupMember(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(true);
        when(expenses.isMemberGroupOwing(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(false);

        String expected = MessageFormat.format(NO_USER_EXPENSE_MESSAGE, TEST_MEMBER);
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_VALID);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetRepayedForGroupCommandOwesLess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(true);
        when(expenses.hasGroupMember(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(true);
        when(expenses.isMemberGroupOwing(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(true);
        when(expenses.getMemberOwing(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(TEST_AMOUNT - 1);

        String expected = MessageFormat.format(FRIEND_OWES_LESS_MESSAGE, TEST_MEMBER, TEST_AMOUNT - 1);
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_VALID);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRepayedGroupCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(true);
        when(expenses.hasGroupMember(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(true);
        when(expenses.isMemberGroupOwing(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(true);
        when(expenses.getMemberOwing(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER)).thenReturn(TEST_AMOUNT + 1);
        doNothing().when(expenses).payedMemberExpense(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER, TEST_AMOUNT);
        when(loginData.isUsernameLogged(TEST_MEMBER)).thenReturn(false);
        doNothing().when(notifications).addApprovedPayment(TEST_MEMBER, TEST_USERNAME, TEST_AMOUNT, TEST_GROUP_NAME);

        String expected = MessageFormat.format(SUCCESSFUL_GROUP_PAYMENT_MESSAGE, TEST_MEMBER, TEST_AMOUNT, TEST_GROUP_NAME, TEST_AMOUNT + 1);
        cmd = new Command(REPAYED_FOR_GROUP_COMMAND_VALID);
        String actual = repayHandler.getRepayedForGroup(testSocket, cmd);

        assertEquals(expected, actual);
        verify(expenses, times(1)).payedMemberExpense(TEST_USERNAME, TEST_GROUP_NAME, TEST_MEMBER, TEST_AMOUNT);
        verify(notifications, times(1)).addApprovedPayment(TEST_MEMBER, TEST_USERNAME, TEST_AMOUNT, TEST_GROUP_NAME);
    }

}
