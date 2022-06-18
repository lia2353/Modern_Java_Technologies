package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.handlers;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.command.Command;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;
import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.RegistrationsData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;

public class RegistrationHandler {

    // Commands
    private static final String REGISTER_COMMAND = "register";

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
    private static final int FIRST_ARGUMENT = 0;
    private static final int SECOND_ARGUMENT = 1;
    private static final int THIRD_ARGUMENT = 2;

    private final RegistrationsData registrations;
    private final LoginData loginData;

    public RegistrationHandler(RegistrationsData registrations, LoginData loginData) {
        this.registrations = registrations;
        this.loginData = loginData;
    }

    public String register(SocketChannel socketOfExecutor, Command command) {
        if (!command.hasThreeArguments()) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REGISTER_COMMAND, THREE_ARGUMENTS);
        }
        if (loginData.isLogged(socketOfExecutor)) {
            return ALREADY_LOGGED_MESSAGE;
        }

        String username = command.getArguments()[FIRST_ARGUMENT];
        String password = command.getArguments()[SECOND_ARGUMENT];
        String email = command.getArguments()[THIRD_ARGUMENT];

        if (registrations.isRegistered(username)) {
            return MessageFormat.format(ALREADY_TAKEN_MESSAGE, USERNAME + username);
        }
        if (!registrations.isValidUsername(username)) {
            return MessageFormat.format(INVALID_ARGUMENT_MESSAGE, USERNAME + username);
        }
        if (registrations.isEmailTaken(username, email)) {
            return MessageFormat.format(ALREADY_TAKEN_MESSAGE, EMAIL + email);
        }
        if (!registrations.isValidEmail(email)) {
            return MessageFormat.format(INVALID_ARGUMENT_MESSAGE, EMAIL + email);
        }
        if (!registrations.isValidPassword(password)) {
            return MessageFormat.format(INVALID_ARGUMENT_MESSAGE, PASSWORD);
        }

        registrations.addRegistration(username, password, email);
        loginData.loginUser(socketOfExecutor, username);
        return MessageFormat.format(SUCCESSFUL_REGISTRATION_MESSAGE, username);
    }
}
