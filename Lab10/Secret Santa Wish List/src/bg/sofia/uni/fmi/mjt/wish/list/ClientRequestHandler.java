package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientRequestHandler implements Runnable {
    // Commands
    private static final String REGISTER_COMMAND = "register";
    private static final String LOGIN_COMMAND = "login";
    private static final String POST_WISH_COMMAND = "post-wish";
    private static final String GET_WISH_COMMAND = "get-wish";
    private static final String LOGOUT_COMMAND = "logout";
    private static final String DISCONNECT_COMMAND = "disconnect";

    private static final String USERNAME_PATTERN = "[a-zA-Z0-9_.-]+";

    // Messages
    private static final String SUCCESSFUL_REGISTRATION_MESSAGE = "[ Username {0} successfully registered ]";
    private static final String DUPLICATE_USERNAME_MESSAGE = "[ Username {0} is already taken, select another one ]";
    private static final String INVALID_USERNAME_MESSAGE = "[ Username {0} is invalid, select a valid one ]";
    private static final String PROVIDE_USERNAME_PASSWORD_MESSAGE = "[ Provide username and password ]";
    private static final String ALREADY_LOGGED_MESSAGE = "[ User {0} already logged. Logout to continue ]";
    private static final String SUCCESSFUL_LOGIN_MESSAGE = "[ User {0} successfully logged in ]";
    private static final String INVALID_USERNAME_PASSWORD_MESSAGE = "[ Invalid username/password combination ]";
    private static final String NOT_LOGGED_MESSAGE = "[ You are not logged in ]";
    private static final String SUCCESSFUL_LOGOUT_MESSAGE = "[ Successfully logged out ]";
    private static final String UNKNOWN_COMMAND_MESSAGE = "[ Unknown command ]";
    private static final String NO_WISHES_MESSAGE = "[ There are no students present in the wish list ]";
    private static final String PROVIDE_USERNAME_WISH_MESSAGE = "[ Provide username and wish ]";
    private static final String USER_NOT_REGISTERED_MESSAGE = "[ Student with username {0} is not registered ]";
    private static final String DUPLICATE_GIFT_MESSAGE = "[ The same gift for student {0} was already submitted ]";
    private static final String SUCCESSFUL_SUBMITTED_GIFT_MESSAGE
            = "[ Gift {0} for student {1} submitted successfully ]";

    private static final Map<String, String> usersRegistry = new HashMap<>();
    private static final Map<String, List<String>> usersWishLists = new HashMap<>();
    private final Socket socket;
    private String loggedUsername;

    public ClientRequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String inputLine;
            while ((inputLine = in.readLine()) != null) { // read the message from the client
                inputLine = inputLine.trim();
                String[] message = inputLine.split("\\s+");
                String command = message[0];

                String replay;
                if (command.equals(REGISTER_COMMAND)) {
                    replay = register(Arrays.copyOfRange(message, 1, message.length));
                } else if (command.equals(LOGIN_COMMAND)) {
                    replay = login(Arrays.copyOfRange(message, 1, message.length));
                } else if (inputLine.equals(LOGOUT_COMMAND)) {
                    replay = logout();
                } else if (command.equals(POST_WISH_COMMAND)) {
                    replay = postWish(Arrays.copyOfRange(message, 1, message.length));
                } else if (inputLine.equals(GET_WISH_COMMAND)) {
                    replay = getWish();
                } else {
                    replay = UNKNOWN_COMMAND_MESSAGE;
                }
                out.println(replay);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String register(String[] args) {
        if (loggedUsername != null) {
            return MessageFormat.format(ALREADY_LOGGED_MESSAGE, loggedUsername);
        }
        if (args.length < 2) {
            return PROVIDE_USERNAME_PASSWORD_MESSAGE;
        }
        if (usersRegistry.containsKey(args[0])) {
            return MessageFormat.format(DUPLICATE_USERNAME_MESSAGE, args[0]);
        }
        if (!args[0].matches(USERNAME_PATTERN)) {
            return MessageFormat.format(INVALID_USERNAME_MESSAGE, args[0]);
        }
        // Success
        usersRegistry.put(args[0], args[1]);
        loggedUsername = args[0];
        return MessageFormat.format(SUCCESSFUL_REGISTRATION_MESSAGE, args[0]);
    }

    private String login(String[] args) {
        if (loggedUsername != null) {
            return MessageFormat.format(ALREADY_LOGGED_MESSAGE, loggedUsername);
        }
        if (args.length < 2) {
            return PROVIDE_USERNAME_PASSWORD_MESSAGE;
        }
        if (!usersRegistry.containsKey(args[0]) || !usersRegistry.get(args[0]).equals(args[1])) {
            return INVALID_USERNAME_PASSWORD_MESSAGE;
        }
        // Success
        loggedUsername = args[0];
        return MessageFormat.format(SUCCESSFUL_LOGIN_MESSAGE, args[0]);
    }

    private String logout() {
        if (loggedUsername == null) {
            return NOT_LOGGED_MESSAGE;
        }
        // Success
        loggedUsername = null;
        return SUCCESSFUL_LOGOUT_MESSAGE;
    }

    private String postWish(String[] args) {
        if (loggedUsername == null) {
            return NOT_LOGGED_MESSAGE;
        }
        if (args.length < 2) {
            return PROVIDE_USERNAME_WISH_MESSAGE;
        }
        String username = args[0];
        String wish = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        List<String> wishesList;
        if (!usersRegistry.containsKey(username)) {
            return MessageFormat.format(USER_NOT_REGISTERED_MESSAGE, username);
        }
        // If there is no wishlist for this user -> create wishlist
        if (usersWishLists.get(username) == null) {
            wishesList = new ArrayList<>();
            wishesList.add(wish);
            usersWishLists.put(username, wishesList);
            return MessageFormat.format(SUCCESSFUL_SUBMITTED_GIFT_MESSAGE, wish, username);
        }
        if (usersWishLists.get(username).contains(wish)) {
            return MessageFormat.format(DUPLICATE_GIFT_MESSAGE, username);
        }
        // If there is already created wishlist for this user
        wishesList = usersWishLists.get(username);
        wishesList.add(wish);
        usersWishLists.put(username, wishesList);
        return MessageFormat.format(SUCCESSFUL_SUBMITTED_GIFT_MESSAGE, wish, username);
    }

    private String getWish() {
        if (loggedUsername == null) {
            return NOT_LOGGED_MESSAGE;
        }
        // There are no wishes or the only wishes are for the logged user
        if (usersWishLists.isEmpty() || (usersWishLists.size() == 1 && usersWishLists.containsKey(loggedUsername))) {
            return NO_WISHES_MESSAGE;
        }
        // Success
        String user;
        do {
            // Random number in [min; max] -> (Math.random() * (max - min)) + min);
            int index = (int) (Math.random() * usersWishLists.size());
            user = new ArrayList<>(usersWishLists.keySet()).get(index);
        } while (user.equals(loggedUsername));

        String wishes = String.join(", ", usersWishLists.get(user));

        usersWishLists.remove(user);
        return String.format("[ %s: [%s] ]", user, wishes);
    }

}
