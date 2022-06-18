package bg.sofia.uni.fmi.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers.RegistrationHandler;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
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

public class RegistrationHandlerTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final String TEST_PASSWORD = "testPassword";
    private static final String TEST_EMAIL = "testEmail";

    // Commands
    private static final String REGISTER_COMMAND = "register";
    private static final String REGISTER_COMMAND_INVALID_ARGUMENTS_COUNT = REGISTER_COMMAND;
    private static final String REGISTER_COMMAND_VALID = REGISTER_COMMAND + " " + TEST_USERNAME + " " + TEST_PASSWORD
            + " " + TEST_EMAIL;
    // Messages
    private static final String SUCCESSFUL_REGISTRATION_MESSAGE =
            "{0}, you are successfully registered in Split(NotSo)Wise!";
    private static final String ALREADY_LOGGED_MESSAGE = "You are already logged. Logout to continue.";
    private static final String INVALID_ARGUMENT_MESSAGE = "{0} is invalid, select a valid one.";
    private static final String ALREADY_TAKEN_MESSAGE = "{0} is already taken, select another one.";
    private static final String USERNAME = "Username ";
    private static final String EMAIL = "Email ";
    private static final String PASSWORD = "Password ";
    private static final String INVALID_ARGS_COUNT_MESSAGE
            = "Invalid arguments count: {0} expects {1} arguments. Refer to help command for more information.";
    private static final int THREE_ARGUMENTS = 3;

    private static RegistrationsData registrations;
    private static LoginData loginData;
    private static SocketChannel testSocket;
    private Command cmd;

    private static RegistrationHandler registrationHandler;

    @Before
    public void setUp() {
        registrations = mock(RegistrationsData.class);
        loginData = mock(LoginData.class);
        testSocket = mock(SocketChannel.class);
        registrationHandler = new RegistrationHandler(registrations, loginData);
    }

    @Test
    public void testRegisterCommandInvalidArgumentsCount() {
        String expected = MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REGISTER_COMMAND, THREE_ARGUMENTS);
        cmd = new Command(REGISTER_COMMAND_INVALID_ARGUMENTS_COUNT);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandAlreadyLoggedMessage() {
        when(loginData.isLogged(testSocket)).thenReturn(true);

        String expected = MessageFormat.format(ALREADY_LOGGED_MESSAGE, TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandDuplicateUsername() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(true);

        String expected = MessageFormat.format(ALREADY_TAKEN_MESSAGE, USERNAME + TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandInvalidUsername() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrations.isValidUsername(TEST_USERNAME)).thenReturn(false);

        String expected = MessageFormat.format(INVALID_ARGUMENT_MESSAGE, USERNAME + TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandDuplicateEmail() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrations.isValidUsername(TEST_USERNAME)).thenReturn(true);
        when(registrations.isEmailTaken(TEST_USERNAME, TEST_EMAIL)).thenReturn(true);

        String expected = MessageFormat.format(ALREADY_TAKEN_MESSAGE, EMAIL + TEST_EMAIL);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandInvalidEmail() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrations.isValidUsername(TEST_USERNAME)).thenReturn(true);
        when(registrations.isEmailTaken(TEST_USERNAME, TEST_EMAIL)).thenReturn(false);
        when(registrations.isValidEmail(TEST_EMAIL)).thenReturn(false);

        String expected = MessageFormat.format(INVALID_ARGUMENT_MESSAGE, EMAIL + TEST_EMAIL);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandInvalidPassword() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrations.isValidUsername(TEST_USERNAME)).thenReturn(true);
        when(registrations.isEmailTaken(TEST_USERNAME, TEST_EMAIL)).thenReturn(false);
        when(registrations.isValidEmail(TEST_EMAIL)).thenReturn(true);
        when(registrations.isValidPassword(TEST_PASSWORD)).thenReturn(false);

        String expected = MessageFormat.format(INVALID_ARGUMENT_MESSAGE, PASSWORD);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
    }

    @Test
    public void testRegisterCommandSuccess() {
        when(loginData.isLogged(testSocket)).thenReturn(false);
        when(registrations.isRegistered(TEST_USERNAME)).thenReturn(false);
        when(registrations.isValidUsername(TEST_USERNAME)).thenReturn(true);
        when(registrations.isEmailTaken(TEST_USERNAME, TEST_EMAIL)).thenReturn(false);
        when(registrations.isValidEmail(TEST_EMAIL)).thenReturn(true);
        when(registrations.isValidPassword(TEST_PASSWORD)).thenReturn(true);
        doNothing().when(registrations).addRegistration(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        doNothing().when(loginData).loginUser(testSocket, TEST_USERNAME);

        String expected = MessageFormat.format(SUCCESSFUL_REGISTRATION_MESSAGE, TEST_USERNAME);
        cmd = new Command(REGISTER_COMMAND_VALID);
        String actual = registrationHandler.register(testSocket, cmd);

        assertEquals(expected, actual);
        verify(registrations, times(1)).addRegistration(TEST_USERNAME, TEST_PASSWORD, TEST_EMAIL);
        verify(loginData, times(1)).loginUser(testSocket, TEST_USERNAME);
    }

}
