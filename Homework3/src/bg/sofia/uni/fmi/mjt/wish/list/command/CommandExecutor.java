package bg.sofia.uni.fmi.mjt.wish.list.command;

import bg.sofia.uni.fmi.mjt.wish.list.storage.LoginData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.RegistrationsData;
import bg.sofia.uni.fmi.mjt.wish.list.storage.WishlistsData;

import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.Arrays;

public class CommandExecutor {
    // Commands
    private static final String REGISTER_COMMAND = "register";
    private static final String LOGIN_COMMAND = "login";
    private static final String LOGOUT_COMMAND = "logout";
    private static final String POST_WISH_COMMAND = "post-wish";
    private static final String GET_WISH_COMMAND = "get-wish";
    private static final String DISCONNECT_COMMAND = "disconnect";

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


    private final RegistrationsData registrationsData;
    private final LoginData loginData;
    private final WishlistsData wishlistsData;

    private Command command;
    private SocketChannel socketOfExecutor;

    public CommandExecutor(RegistrationsData registrationsData, LoginData loginData, WishlistsData wishlistsData) {
        this.registrationsData = registrationsData;
        this.loginData = loginData;
        this.wishlistsData = wishlistsData;
    }

    public String execute(Command cmd, SocketChannel socket) {
        command = cmd;
        socketOfExecutor = socket;
        return switch (cmd.getName()) {
            case REGISTER_COMMAND -> register();
            case LOGIN_COMMAND -> login();
            case LOGOUT_COMMAND -> logout();
            case POST_WISH_COMMAND -> postWish();
            case GET_WISH_COMMAND -> getWish();
            case DISCONNECT_COMMAND -> disconnect();
            default -> UNKNOWN_COMMAND_MESSAGE;
        };
    }

    private String register() {
        if (command.getArgumentsCount() != TWO_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, REGISTER_COMMAND,
                    TWO_ARGUMENTS, USERNAME_AND_PASSWORD);
        }
        if (loginData.isLogged(socketOfExecutor)) {
            return MessageFormat.format(ALREADY_LOGGED_MESSAGE, loginData.getLoggedUsername(socketOfExecutor));
        }
        String username = command.getFirstArgument();
        String password = command.getSecondArgument();
        if (registrationsData.isRegistered(username)) {
            return MessageFormat.format(DUPLICATE_USERNAME_MESSAGE, username);
        }
        if (!registrationsData.isValidUsername(username)) {
            return MessageFormat.format(INVALID_USERNAME_MESSAGE, username);
        }
        // Success
        registrationsData.addRegistration(username, password);
        loginData.loginUser(socketOfExecutor, username);
        return MessageFormat.format(SUCCESSFUL_REGISTRATION_MESSAGE, username);
    }

    private String login() {
        if (command.getArgumentsCount() != TWO_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGIN_COMMAND,
                    TWO_ARGUMENTS, USERNAME_AND_PASSWORD);
        }
        if (loginData.isLogged(socketOfExecutor)) {
            return MessageFormat.format(ALREADY_LOGGED_MESSAGE, loginData.getLoggedUsername(socketOfExecutor));
        }
        String username = command.getFirstArgument();
        String password = command.getSecondArgument();
        if (!registrationsData.isRegistered(username)
                || !registrationsData.isAuthenticationSuccessful(username, password)) {
            return INVALID_USERNAME_PASSWORD_MESSAGE;
        }
        // Success
        loginData.loginUser(socketOfExecutor, username);
        return MessageFormat.format(SUCCESSFUL_LOGIN_MESSAGE, username);
    }

    private String logout() {
        if (command.getArgumentsCount() != ZERO_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, LOGOUT_COMMAND,
                    ZERO_ARGUMENTS, EMPTY_STRING);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }
        // Success
        loginData.logoutUser(socketOfExecutor);
        return SUCCESSFUL_LOGOUT_MESSAGE;
    }

    private String disconnect() {
        if (command.getArgumentsCount() != ZERO_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, DISCONNECT_COMMAND,
                    ZERO_ARGUMENTS, EMPTY_STRING);
        }
        // Success
        loginData.logoutUser(socketOfExecutor);
        return DISCONNECT_MESSAGE;
    }

    private String postWish() {
        if (command.getArgumentsCount() < TWO_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, POST_WISH_COMMAND,
                    TWO_ARGUMENTS, USERNAME_AND_WISH);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }
        String username = command.getFirstArgument();
        String wish = String.join(" ", Arrays.copyOfRange(command.getArguments(), 1, command.getArgumentsCount()));
        if (!registrationsData.isRegistered(username)) {
            return MessageFormat.format(USER_NOT_REGISTERED_MESSAGE, username);
        }
        if (wishlistsData.alreadyContainsWish(username, wish)) {
            return MessageFormat.format(DUPLICATE_GIFT_MESSAGE, username);
        }
        // Success
        wishlistsData.addWish(username, wish);
        return MessageFormat.format(SUCCESSFUL_SUBMITTED_GIFT_MESSAGE, wish, username);
    }

    private String getWish() {
        if (command.getArgumentsCount() != ZERO_ARGUMENTS) {
            return MessageFormat.format(INVALID_ARGS_COUNT_MESSAGE, GET_WISH_COMMAND,
                    ZERO_ARGUMENTS, EMPTY_STRING);
        }
        if (!loginData.isLogged(socketOfExecutor)) {
            return NOT_LOGGED_MESSAGE;
        }
        String loggedUsername = loginData.getLoggedUsername(socketOfExecutor);
        // There are no wishes or the only wishes are for the logged user
        if (wishlistsData.isEmpty() || wishlistsData.containsOnlyUsersWishlist(loggedUsername)) {
            return NO_WISHES_MESSAGE;
        }
        // Success
        String username = wishlistsData.getRandomUsernameWithWishList(loggedUsername);
        String wishes = wishlistsData.getWishlist(username);
        wishlistsData.removeWishlist(username);
        return MessageFormat.format(WISHLIST_MESSAGE, username, wishes);
    }

}
