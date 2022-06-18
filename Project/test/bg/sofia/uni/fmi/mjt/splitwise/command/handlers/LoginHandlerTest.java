package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.LoginHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

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

public class LoginHandlerTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";

    // Commands
    private static final String LOGIN_COMMAND = "login";
    private static final String LOGIN_COMMAND_INVALID_ARGUMENTS_COUNT = LOGIN_COMMAND;
    private static final String LOGIN_COMMAND_VALID = LOGIN_COMMAND + " " + TEST_USERNAME + " " + TEST_PASSWORD;
    private static final String LOGOUT_COMMAND = "logout";
    private static final String EXIT_COMMAND = "exit";

    // Messages
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "Hi {0}!";
    private static final String ALREADY_LOGGED_MESSAGE = "You are already logged. Logout to continue.";
    private static final String INVALID_USERNAME_PASSWORD_MESSAGE = "Invalid username and password combination.";
    private static final String NOT_LOGGED_MESSAGE = "Please login to continue.";
    private static final String SUCCESSFUL_LOGOUT_MESSAGE = "Successfully logged out.";
    private static final String EXIT_MESSAGE = "Bye bye and don't forget to smile!";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int ZERO_ARGUMENTS = 0;
    private static final int TWO_ARGUMENTS = 2;

    private static RegistrationsData registrations;
    private static LoginData loginData;
    private static NotificationsData notifications;

    private static SocketChannel testSocket;
    private Command cmd;

    private static LoginHandler loginHandler;

    @Before
    public void setUp() {
        registrations = mock(RegistrationsData.class);
        loginData = mock(LoginData.class);
        notifications = mock(NotificationsData.class);
        testSocket = mock(SocketChannel.class);
        loginHandler = new LoginHandler(registrations, loginData, notifications);
    }

    // LOGIN_COMMAND
    @Test
    public void testLoginCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGIN_COMMAND, TWO_ARGUMENTS);
        cmd = new Command(LOGIN_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = loginHandler.login(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testLoginCommandAlreadyLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(true);

        String expected = ALREADY_LOGGED_MESSAGE;
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = loginHandler.login(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testLoginCommandNoSuchUsername() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(false);

        String expected = INVALID_USERNAME_PASSWORD_MESSAGE;
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = loginHandler.login(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testLoginCommandNoSuchPassword() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(registrations.isAuthenticationSuccessful(TEST_USERNAME, TEST_PASSWORD)).thenReturn(false);

        String expected = INVALID_USERNAME_PASSWORD_MESSAGE;
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = loginHandler.login(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testLoginCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(registrations.isAuthenticationSuccessful(TEST_USERNAME, TEST_PASSWORD)).thenReturn(true);
        doNothing().when(loginData).loginUser(testSocket, TEST_USERNAME);
        when(notifications.getNotifications(TEST_USERNAME)).thenReturn("");

        String expected = MessageFormat.format(SUCCESSFUL_LOGIN_MESSAGE, TEST_USERNAME) + System.lineSeparator();
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = loginHandler.login(testSocket, cmd);

        assertEquals(expected, actual);
        verify(loginData, times(1)).loginUser(testSocket, TEST_USERNAME);
    }

    // LOGOUT_COMMAND
    @Test
    public void testLogoutCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGOUT_COMMAND, ZERO_ARGUMENTS);
        cmd = new Command(LOGOUT_COMMAND + " " + LOGIN_COMMAND);
        String actual = loginHandler.logout(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testLogoutCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(LOGOUT_COMMAND);
        String actual = loginHandler.logout(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testLogoutCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        doNothing().when(loginData).logoutUser(testSocket);

        String expected = SUCCESSFUL_LOGOUT_MESSAGE;
        cmd = new Command(LOGOUT_COMMAND);
        String actual = loginHandler.logout(testSocket, cmd);

        assertEquals(expected, actual);
        verify(loginData, times(1)).logoutUser(testSocket);
    }

    // EXIT_COMMAND
    @Test
    public void testExitCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, EXIT_COMMAND, ZERO_ARGUMENTS);
        cmd = new Command(EXIT_COMMAND + " " + EXIT_COMMAND);
        String actual = loginHandler.exit(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testExitCommandSuccess() {
        doNothing().when(loginData).logoutUser(testSocket);

        String expected = EXIT_MESSAGE;
        cmd = new Command(EXIT_COMMAND);
        String actual = loginHandler.exit(testSocket, cmd);

        assertEquals(expected, actual);
        verify(loginData, times(1)).logoutUser(testSocket);
    }

}
