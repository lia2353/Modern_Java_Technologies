package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

public class LoginHandler {

    // Commands
    private static final String LOGIN_COMMAND = "login";
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
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;

    private final RegistrationsData registrations;
    private final LoginData loginData;
    private final NotificationsData notifications;

    public LoginHandler(RegistrationsData registrations, LoginData loginData, NotificationsData notifications) {
        this.registrations = registrations;
        this.loginData = loginData;
        this.notifications = notifications;
    }

    public String login(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasTwoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGIN_COMMAND, TWO_ARGUMENTS);
        }
        if (loginData.isLogged(socketOfExecutor)) {
            return ALREADY_LOGGED_MESSAGE;
        }

        String username = command.getArguments()[FIRST_ARGUMENT];
        String password = command.getArguments()[SECOND_ARGUMENT];
        if (!registrations.isRegistered(username)
                || !registrations.isAuthenticationSuccessful(username, password)) {
            return INVALID_USERNAME_PASSWORD_MESSAGE;
        }

        loginData.loginUser(socketOfExecutor, username);
        return MessageFormat.format(SUCCESSFUL_LOGIN_MESSAGE, username) + System.lineSeparator()
                + notifications.getNotifications(username);
    }

    public String logout(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasNoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGOUT_COMMAND, ZERO_ARGUMENTS);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }

        loginData.logoutUser(socketOfExecutor);
        return SUCCESSFUL_LOGOUT_MESSAGE;
    }

    public String exit(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasNoArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, EXIT_COMMAND, ZERO_ARGUMENTS);
        }

        loginData.logoutUser(socketOfExecutor);
        return EXIT_MESSAGE;
    }
}
