package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.storage.LoginData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.RegistrationsData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.WishlistsData;

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

public class CommandExecutorTest {
    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_WISH = "test wish";

    // Commands
    private static final String REGISTER_COMMAND = "register";
    private static final String REGISTER_COMMAND_INVALID_ARGUMENTS_COUNT = REGISTER_COMMAND;
    private static final String REGISTER_COMMAND_VALID = REGISTER_COMMAND + " " + TEST_USERNAME + " " + TEST_PASSWORD;
    private static final String LOGIN_COMMAND = "login";
    private static final String LOGIN_COMMAND_INVALID_ARGUMENTS_COUNT = LOGIN_COMMAND;
    private static final String LOGIN_COMMAND_VALID = LOGIN_COMMAND + " " + TEST_USERNAME + " " + TEST_PASSWORD;
    private static final String LOGOUT_COMMAND = "logout";
    private static final String POST_WISH_COMMAND = "post-wish";
    private static final String POST_WISH_COMMAND_INVALID_ARGUMENTS_COUNT = POST_WISH_COMMAND;
    private static final String POST_WISH_COMMAND_VALID = POST_WISH_COMMAND + " " + TEST_USERNAME + " " + TEST_WISH;
    private static final String GET_WISH_COMMAND = "get-wish";
    private static final String DISCONNECT_COMMAND = "disconnect";
    private static final String UNKNOWN_COMMAND = "unknown command";

    // Messages
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "[ Invalid arguments count: {0} expects {1} arguments{2} ]";
    private static final String USERNAME_AND_PASSWORD = ": <username> <password>";
    private static final String USERNAME_AND_WISH = ": <username> <wish>";
    private static final String EMPTY_STRING = "";
    private static final int ZERO_ARGUMENTS = 0;
    private static final int TWO_ARGUMENTS = 2;
    private static final String SUCCESSFUL_REGISTRATION_MESSAGE = "[ Username {0} successfully registered ]";
    private static final String DUPLICATE_USERNAME_MESSAGE = "[ Username {0} is already taken, select another one ]";
    private static final String INVALID_USERNAME_MESSAGE = "[ Username {0} is invalid, select a valid one ]";
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "[ User {0} successfully logged in ]";
    private static final String ALREADY_LOGGED_MESSAGE = "[ User {0} already logged. Logout to continue ]";
    private static final String INVALID_USERNAME_PASSWORD_MESSAGE = "[ Invalid username/password combination ]";
    private static final String SUCCESSFUL_LOGOUT_MESSAGE = "[ Successfully logged out ]";
    private static final String NOT_LOGGED_MESSAGE = "[ You are not logged in ]";
    private static final String USER_NOT_REGISTERED_MESSAGE = "[ Student with username {0} is not registered ]";
    private static final String SUCCESSFUL_SUBMITTED_GIFT_MESSAGE
            = "[ Gift {0} for student {1} submitted successfully ]";
    private static final String DUPLICATE_GIFT_MESSAGE = "[ The same gift for student {0} was already submitted ]";
    private static final String WISHLIST_MESSAGE = "[ {0}: [{1}] ]";
    private static final String NO_WISHES_MESSAGE = "[ There are no students present in the wish list ]";
    private static final String DISCONNECT_MESSAGE = "[ Disconnected from server ]";
    private static final String UNKNOWN_COMMAND_MESSAGE = "[ Unknown command ]";

    private static RegistrationsData registrationsData;
    private static LoginData loginData;
    private static WishlistsData wishlistsData;

    private static SocketChannel testSocket;
    private static CommandExecutor cmdExec;
    private Command cmd;

    @Before
    public void setUp() {
        registrationsData = mock(RegistrationsData.class);
        loginData = mock(LoginData.class);
        wishlistsData = mock(WishlistsData.class);
        testSocket = mock(SocketChannel.class);
        cmdExec = new CommandExecutor(registrationsData, loginData, wishlistsData);
    }

    // REGISTER_COMMAND
    @Test
    public void testExecuteRegisterCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REGISTER_COMMAND,
                TWO_ARGUMENTS, USERNAME_AND_PASSWORD);
        cmd = new Command(REGISTER_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteRegisterCommandAlreadyLoggedMessage() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);

        String expected = MessageFormat.format(ALREADY_LOGGED_MESSAGE, TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteRegisterCommandDuplicateUsername() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);

        String expected = MessageFormat.format(DUPLICATE_USERNAME_MESSAGE, TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteRegisterCommandInvalidUsername() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrationsData.isValidUsername(TEST_USERNAME)).thenReturn(false);

        String expected = MessageFormat.format(INVALID_USERNAME_MESSAGE, TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteRegisterCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrationsData.isValidUsername(TEST_USERNAME)).thenReturn(true);
        doNothing().when(registrationsData).addRegistration(TEST_USERNAME, TEST_PASSWORD);
        doNothing().when(loginData).loginUser(testSocket, TEST_USERNAME);

        String expected = MessageFormat.format(SUCCESSFUL_REGISTRATION_MESSAGE, TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
        verify(registrationsData, times(1)).addRegistration(TEST_USERNAME, TEST_PASSWORD);
        verify(loginData, times(1)).loginUser(testSocket, TEST_USERNAME);
    }

    // LOGIN_COMMAND
    @Test
    public void testExecuteLoginCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGIN_COMMAND,
                TWO_ARGUMENTS, USERNAME_AND_PASSWORD);
        cmd = new Command(LOGIN_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteLoginCommandAlreadyLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);

        String expected = MessageFormat.format(ALREADY_LOGGED_MESSAGE, TEST_USERNAME);
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteLoginCommandNoSuchUsername() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(false);

        String expected = INVALID_USERNAME_PASSWORD_MESSAGE;
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteLoginCommandNoSuchPassword() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(registrationsData.isAuthenticationSuccessful(TEST_USERNAME, TEST_PASSWORD)).thenReturn(false);

        String expected = INVALID_USERNAME_PASSWORD_MESSAGE;
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteLoginCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(registrationsData.isAuthenticationSuccessful(TEST_USERNAME, TEST_PASSWORD)).thenReturn(true);
        doNothing().when(loginData).loginUser(testSocket, TEST_USERNAME);

        String expected = MessageFormat.format(SUCCESSFUL_LOGIN_MESSAGE, TEST_USERNAME);
        cmd = new Command(LOGIN_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
        verify(loginData, times(1)).loginUser(testSocket, TEST_USERNAME);
    }

    // LOGOUT_COMMAND
    @Test
    public void testExecuteLogoutCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGOUT_COMMAND,
                ZERO_ARGUMENTS, EMPTY_STRING);
        cmd = new Command(LOGOUT_COMMAND + " " + LOGIN_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteLogoutCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(LOGOUT_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteLogoutCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        doNothing().when(loginData).logoutUser(testSocket);

        String expected = SUCCESSFUL_LOGOUT_MESSAGE;
        cmd = new Command(LOGOUT_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
        verify(loginData, times(1)).logoutUser(testSocket);
    }

    // POST_WISH_COMMAND
    @Test
    public void testExecutePostWishCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, POST_WISH_COMMAND,
                TWO_ARGUMENTS, USERNAME_AND_WISH);
        cmd = new Command(POST_WISH_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecutePostWishCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(POST_WISH_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecutePostWishCommandNotRegistered() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(false);

        String expected = MessageFormat.format(USER_NOT_REGISTERED_MESSAGE, TEST_USERNAME);
        cmd = new Command(POST_WISH_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecutePostWishCommandDuplicateGift() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(wishlistsData.alreadyContainsWish(TEST_USERNAME, TEST_WISH)).thenReturn(true);

        String expected = MessageFormat.format(DUPLICATE_GIFT_MESSAGE, TEST_USERNAME);
        cmd = new Command(POST_WISH_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecutePostWishCommandSuccessOneGift() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(wishlistsData.alreadyContainsWish(TEST_USERNAME, TEST_WISH)).thenReturn(false);
        doNothing().when(wishlistsData).addWish(TEST_USERNAME, TEST_WISH);

        String expected = MessageFormat.format(SUCCESSFUL_SUBMITTED_GIFT_MESSAGE, TEST_WISH, TEST_USERNAME);
        cmd = new Command(POST_WISH_COMMAND_VALID);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
        verify(wishlistsData, times(1)).addWish(TEST_USERNAME, TEST_WISH);
    }

    @Test
    public void testExecutePostWishCommandSuccessManyGifts() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(wishlistsData.alreadyContainsWish(TEST_USERNAME, TEST_WISH)).thenReturn(false);
        doNothing().when(wishlistsData).addWish(TEST_USERNAME, TEST_WISH);

        String expected = MessageFormat.format(SUCCESSFUL_SUBMITTED_GIFT_MESSAGE, TEST_WISH, TEST_USERNAME);
        cmd = new Command(POST_WISH_COMMAND_VALID);
        String actual1 = cmdExec.execute(cmd, testSocket);
        String actual2 = cmdExec.execute(cmd, testSocket);
        String actual3 = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual1);
        assertEquals(expected, actual2);
        assertEquals(expected, actual3);
        verify(wishlistsData, times(3)).addWish(TEST_USERNAME, TEST_WISH);
    }

    // GET_WISH_COMMAND
    @Test
    public void testExecuteGetWishCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, GET_WISH_COMMAND,
                ZERO_ARGUMENTS, EMPTY_STRING);
        cmd = new Command(GET_WISH_COMMAND + " " + GET_WISH_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteGetWishCommandNotLogged() {
        when(loginData.isLogged(testSocket)).thenReturn(false);

        String expected = NOT_LOGGED_MESSAGE;
        cmd = new Command(GET_WISH_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteGetWishCommandNoWishes() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(wishlistsData.isEmpty()).thenReturn(true);

        String expected = NO_WISHES_MESSAGE;
        cmd = new Command(GET_WISH_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteGetWishCommandOnlyCurrentUserWishes() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(wishlistsData.isEmpty()).thenReturn(false);
        when(wishlistsData.containsOnlyUsersWishlist(TEST_USERNAME)).thenReturn(true);

        String expected = NO_WISHES_MESSAGE;
        cmd = new Command(GET_WISH_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecutePostWishCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(true);
        when(registrationsData.isRegistered(TEST_USERNAME)).thenReturn(true);
        when(loginData.getLoggedUsername(testSocket)).thenReturn(TEST_USERNAME);
        when(wishlistsData.isEmpty()).thenReturn(false);
        when(wishlistsData.containsOnlyUsersWishlist(TEST_USERNAME)).thenReturn(false);
        when(wishlistsData.getRandomUsernameWithWishList(TEST_USERNAME)).thenReturn(TEST_USERNAME);
        when(wishlistsData.getWishlist(TEST_USERNAME)).thenReturn(TEST_WISH);

        String expected = MessageFormat.format(WISHLIST_MESSAGE, TEST_USERNAME, TEST_WISH);
        cmd = new Command(GET_WISH_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }


    // DISCONNECT_COMMAND
    @Test
    public void testExecuteDisconnectCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, DISCONNECT_COMMAND,
                ZERO_ARGUMENTS, EMPTY_STRING);
        cmd = new Command(DISCONNECT_COMMAND + " " + DISCONNECT_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    @Test
    public void testExecuteDisconnectCommandSuccess() {
        String expected = DISCONNECT_MESSAGE;
        cmd = new Command(DISCONNECT_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

    // UNKNOWN_COMMAND
    @Test
    public void testExecuteUnknownCommand() {
        String expected = UNKNOWN_COMMAND_MESSAGE;
        cmd = new Command(UNKNOWN_COMMAND);
        String actual = cmdExec.execute(cmd, testSocket);

        assertEquals(expected, actual);
    }

}
