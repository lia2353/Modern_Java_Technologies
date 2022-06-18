package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.StatusHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.ExpensesData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusHandlerTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_FRIENDS_STATUS = "test friends status";
    private static final String TEST_GROUPS_STATUS = "test groups statuus";

    // Commands
    private static final String GET_STATUS_COMMAND = "get-status";

    // Messages
    private static final String STATUS = "*** Status ***";
    private static final String NOTHING_TO_SHOW = "All settled up!";
    private static final String FRIENDS = "Friends: ";
    private static final String GROUPS = "Groups: ";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ZERO_ARGUMENTS = 0;

    private static LoginData loginData;
    private static ExpensesData expenses;

    private static SocketChannel testSocket;
    private Command cmd;

    private static StatusHandler statusHandler;

    @Before
    public void setUp() {
        loginData = mock(LoginData.class);
        expenses = mock(ExpensesData.class);
        testSocket = mock(SocketChannel.class);

        statusHandler = new StatusHandler(loginData, expenses);
    }

    // GET_STATUS_COMMAND
    @Test
    public void testGetStatusCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, GET_STATUS_COMMAND, ZERO_ARGUMENTS);
        cmd = new Command(GET_STATUS_COMMAND + " " + GET_STATUS_COMMAND);
        String actual = statusHandler.getStatus(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetStatusCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(GET_STATUS_COMMAND);
        String actual = statusHandler.getStatus(testSocket, cmd);

        assertEquals(expected, actual);
    }


    @Test
    public void testGetStatusCommandNothingToShow() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.getFriendsExpensesStatus(TEST_USERNAME)).thenReturn("");
        when(expenses.getGroupsExpensesStatus(TEST_USERNAME)).thenReturn("");

        String expected = STATUS + System.lineSeparator() + NOTHING_TO_SHOW;
        cmd = new Command(GET_STATUS_COMMAND);
        String actual = statusHandler.getStatus(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetStatusCommandOnlyFriendsStatusToShow() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.getFriendsExpensesStatus(TEST_USERNAME)).thenReturn(TEST_FRIENDS_STATUS);
        when(expenses.getGroupsExpensesStatus(TEST_USERNAME)).thenReturn("");

        String expected = STATUS + System.lineSeparator() + FRIENDS + TEST_FRIENDS_STATUS;
        cmd = new Command(GET_STATUS_COMMAND);
        String actual = statusHandler.getStatus(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetStatusCommandOnlyGroupsToShow() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.getFriendsExpensesStatus(TEST_USERNAME)).thenReturn("");
        when(expenses.getGroupsExpensesStatus(TEST_USERNAME)).thenReturn(TEST_GROUPS_STATUS);

        String expected = STATUS + System.lineSeparator() + GROUPS + TEST_GROUPS_STATUS;
        cmd = new Command(GET_STATUS_COMMAND);
        String actual = statusHandler.getStatus(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetStatusCommandFriendsAndGroupsStatusToShow() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(expenses.getFriendsExpensesStatus(TEST_USERNAME)).thenReturn(TEST_FRIENDS_STATUS);
        when(expenses.getGroupsExpensesStatus(TEST_USERNAME)).thenReturn(TEST_GROUPS_STATUS);

        String expected = STATUS + System.lineSeparator() + FRIENDS + TEST_FRIENDS_STATUS + System.lineSeparator()
                + GROUPS + TEST_GROUPS_STATUS;
        cmd = new Command(GET_STATUS_COMMAND);
        String actual = statusHandler.getStatus(testSocket, cmd);

        assertEquals(expected, actual);
    }

}
