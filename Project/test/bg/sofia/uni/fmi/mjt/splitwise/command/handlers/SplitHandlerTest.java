package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.Group;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.SplitHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.PaymentsLogData;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SplitHandlerTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_FRIEND_NAME = "testFriendName";
    private static final String TEST_REASON = "reason";
    private static final String TEST_GROUP_NAME = "testGroupName";
    private static final String TEST_MEMBER = "testMember";
    private static final Double TEST_AMOUNT = 4.2;
    private static final Double TEST_AMOUNT_INVALID = -4.2;
    private static final String TEST_PAYMENTS_LIST = "payments list";

    // Commands
    private static final String SPLIT_COMMAND = "split";
    private static final String SPLIT_COMMAND_INVALID_ARGUMENTS_COUNT = SPLIT_COMMAND;
    private static final String SPLIT_COMMAND_INVALID_AMOUNT_TYPE = SPLIT_COMMAND + " " + TEST_AMOUNT_INVALID + " "
            + TEST_FRIEND_NAME + " " + TEST_REASON;
    private static final String SPLIT_COMMAND_VALID = SPLIT_COMMAND + " " + TEST_AMOUNT + " " + TEST_FRIEND_NAME + " "
            + TEST_REASON;
    private static final String SPLIT_GROUP_COMMAND = "split-group";
    private static final String SPLIT_GROUP_COMMAND_INVALID_ARGUMENTS_COUNT = SPLIT_GROUP_COMMAND;
    private static final String SPLIT_GROUP_COMMAND_INVALID_AMOUNT_TYPE = SPLIT_GROUP_COMMAND + " "
            + TEST_AMOUNT_INVALID + " " + TEST_GROUP_NAME + " " + TEST_REASON;
    private static final String SPLIT_GROUP_COMMAND_VALID = SPLIT_GROUP_COMMAND + " " + TEST_AMOUNT + " "
            + TEST_GROUP_NAME + " " + TEST_REASON;
    private static final String SHOW_PAYMENTS_LOG_COMMAND = "show-payments";

    // Messages
    private static final String SUCCESSFUL_SPLIT_MESSAGE = "Split {0} LV between you and {1}.";
    private static final String SUCCESSFUL_GROUP_SPLIT_MESSAGE = "Split {0} LV between members in {1} group";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String NO_SUCH_FRIEND_MESSAGE = "You are not friends with {0}.";
    private static final String NO_SUCH_GROUP_MESSAGE = "You have no group named {0}.";
    private static final String NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE
            = "Provide valid number for amount argument. Refer to help command for more information.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ZERO_ARGUMENTS = 0;
    private static final int THREE_ARGUMENTS = 3;
    private static final String PAYMENTS = "*** Payments ***";
    private static final String NO_PAYMENTS_TO_SHOW_MESSAGE = "There are no payments.";

    private static LoginData loginData;
    private static ExpensesData expenses;
    private static PaymentsLogData payments;
    private static NotificationsData notifications;

    private static SocketChannel testSocket;
    private Command cmd;

    private static SplitHandler splitHandler;

    @Before
    public void setUp() {
        loginData = mock(LoginData.class);
        expenses = mock(ExpensesData.class);
        payments = mock(PaymentsLogData.class);
        notifications = mock(NotificationsData.class);
        testSocket = mock(SocketChannel.class);

        splitHandler = new SplitHandler(loginData, expenses, payments, notifications);
    }

    // SPLIT_COMMAND
    @Test
    public void testSplitCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SPLIT_COMMAND, THREE_ARGUMENTS);
        cmd = new Command(SPLIT_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = splitHandler.split(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(SPLIT_COMMAND_VALID);
        String actual = splitHandler.split(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitCommandInvalidAmountType() {
        when(loginData.isLogged(testSocket)).thenReturn(true);

        String expected = NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        cmd = new Command(SPLIT_COMMAND_INVALID_AMOUNT_TYPE);
        String actual = splitHandler.split(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitCommandNotFriends() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_FRIEND_MESSAGE, TEST_FRIEND_NAME);
        cmd = new Command(SPLIT_COMMAND_VALID);
        String actual = splitHandler.split(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.areFriends(TEST_USERNAME, TEST_FRIEND_NAME)).thenReturn(true);
        when(expenses.splitFriendExpense(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT)).thenReturn(TEST_AMOUNT / 2);
        doNothing().when(payments).addPayment(false, TEST_USERNAME, TEST_AMOUNT, TEST_FRIEND_NAME,
                TEST_REASON, LocalDate.now());
        when(loginData.isUsernameLogged(TEST_FRIEND_NAME)).thenReturn(false);
        doNothing().when(notifications).addPayment(TEST_FRIEND_NAME, TEST_USERNAME, TEST_AMOUNT / 2, TEST_REASON, null);

        String expected = MessageFormat.format(SUCCESSFUL_SPLIT_MESSAGE, TEST_AMOUNT, TEST_FRIEND_NAME);
        cmd = new Command(SPLIT_COMMAND_VALID);
        String actual = splitHandler.split(testSocket, cmd);

        assertEquals(expected, actual);
        verify(payments, times(1)).addPayment(false, TEST_USERNAME, TEST_AMOUNT,
                TEST_FRIEND_NAME, TEST_REASON, LocalDate.now());
        verify(notifications, times(1)).addPayment(TEST_FRIEND_NAME, TEST_USERNAME, TEST_AMOUNT / 2, TEST_REASON, null);
    }

    // SPLIT_GROUP_COMMAND
    @Test
    public void testSplitGroupCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SPLIT_GROUP_COMMAND, THREE_ARGUMENTS);
        cmd = new Command(SPLIT_GROUP_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = splitHandler.splitGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitGroupCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(SPLIT_GROUP_COMMAND_VALID);
        String actual = splitHandler.splitGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitGroupCommandInvalidAmountType() {
        when(loginData.isLogged(testSocket)).thenReturn(true);

        String expected = NOT_DECIMAL_ARGUMENT_TYPE_MESSAGE;
        cmd = new Command(SPLIT_GROUP_COMMAND_INVALID_AMOUNT_TYPE);
        String actual = splitHandler.splitGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitGroupCommandNoSuchGroup() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(false);

        String expected = MessageFormat.format(NO_SUCH_GROUP_MESSAGE, TEST_GROUP_NAME);
        cmd = new Command(SPLIT_GROUP_COMMAND_VALID);
        String actual = splitHandler.splitGroup(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testSplitGroupCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.hasGroup(TEST_USERNAME, TEST_GROUP_NAME)).thenReturn(true);
        when(expenses.splitGroupExpense(TEST_USERNAME, TEST_GROUP_NAME, TEST_AMOUNT)).thenReturn(TEST_AMOUNT / 3);
        doNothing().when(payments).addPayment(true, TEST_USERNAME, TEST_AMOUNT, TEST_FRIEND_NAME,
                TEST_REASON, LocalDate.now());
        when(expenses.getGroup(TEST_USERNAME, TEST_GROUP_NAME))
                .thenReturn(new Group(TEST_GROUP_NAME, TEST_USERNAME, Set.of(TEST_MEMBER)));
        when(loginData.isUsernameLogged(TEST_MEMBER)).thenReturn(false);
        doNothing().when(notifications).addPayment(TEST_MEMBER, TEST_USERNAME, TEST_AMOUNT / 3, TEST_REASON,
                TEST_GROUP_NAME);

        String expected = MessageFormat.format(SUCCESSFUL_GROUP_SPLIT_MESSAGE, TEST_AMOUNT, TEST_GROUP_NAME);
        cmd = new Command(SPLIT_GROUP_COMMAND_VALID);
        String actual = splitHandler.splitGroup(testSocket, cmd);

        assertEquals(expected, actual);
        verify(payments, times(1)).addPayment(true, TEST_USERNAME, TEST_AMOUNT,
                TEST_GROUP_NAME, TEST_REASON, LocalDate.now());
        verify(notifications, times(1)).addPayment(TEST_MEMBER, TEST_USERNAME, TEST_AMOUNT / 3,
                TEST_REASON, TEST_GROUP_NAME);
    }

    // SHOW_PAYMENTS_LOG_COMMAND
    @Test
    public void testShowPaymentsCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, SHOW_PAYMENTS_LOG_COMMAND, ZERO_ARGUMENTS);
        cmd = new Command(SHOW_PAYMENTS_LOG_COMMAND + " " + SHOW_PAYMENTS_LOG_COMMAND);
        String actual = splitHandler.showPayments(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowPaymentsCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(SHOW_PAYMENTS_LOG_COMMAND);
        String actual = splitHandler.showPayments(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowPaymentsCommandNoPayments() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(payments.hasNoPayments(TEST_USERNAME)).thenReturn(true);

        String expected = NO_PAYMENTS_TO_SHOW_MESSAGE;
        cmd = new Command(SHOW_PAYMENTS_LOG_COMMAND);
        String actual = splitHandler.showPayments(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testShowPaymentsCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(payments.hasNoPayments(TEST_USERNAME)).thenReturn(false);
        when(payments.getPayments(TEST_USERNAME)).thenReturn(TEST_PAYMENTS_LIST);

        String expected = PAYMENTS + System.lineSeparator() + TEST_PAYMENTS_LIST;
        cmd = new Command(SHOW_PAYMENTS_LOG_COMMAND);
        String actual = splitHandler.showPayments(testSocket, cmd);

        assertEquals(expected, actual);
    }
}
