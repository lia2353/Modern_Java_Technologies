package bg.sofia.uni.fmi.mjt.wish.list;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WishListServer {
    // Commands
    private static final String REGISTER_COMMAND = "register";
    private static final String LOGIN_COMMAND = "login";
    private static final String POST_WISH_COMMAND = "post-wish";
    private static final String GET_WISH_COMMAND = "get-wish";
    private static final String LOGOUT_COMMAND = "logout";
    private static final String DISCONNECT_COMMAND = "disconnect";

    private static final String USERNAME_PATTERN = "[a-zA-Z0-9\\_\\.\\-]+";

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
    private static final String WISHLIST_MESSAGE = "[ {0}: [{1}] ]";
    private static final String USER_NOT_REGISTERED_MESSAGE = "[ Student with username {0} is not registered ]";
    private static final String DUPLICATE_GIFT_MESSAGE = "[ The same gift for student {0} was already submitted ]";
    private static final String SUCCESSFUL_SUBMITTED_GIFT_MESSAGE
            = "[ Gift {0} for student {1} submitted successfully ]";
    private static final String DISCONNECT_MESSAGE = "[ Disconnected from server ]";

    private static final String SERVER_HOST = "localhost";
    public int serverPort;
    private static final int BUFFER_SIZE = 1024;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private ByteBuffer buffer;

    private static final Map<String, String> registeredUsers = new HashMap<>();
    private static final Map<String, List<String>> usersWishLists = new HashMap<>();
    private static final Map<SocketChannel, String> clients = new HashMap<>();


    public WishListServer(int serverPort) {
        this.serverPort = serverPort;
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress(SERVER_HOST, serverPort));
            serverSocket.configureBlocking(false); // Configure server in non-blocking mode
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            //System.out.println("Server started and listening for connect requests");
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            buffer = ByteBuffer.allocate(BUFFER_SIZE); // non-direct buffer

            while (true) {
                int readyChannels = selector.select(); // blocking method
                if (readyChannels == 0) {
                    // select() is blocking, but in 3 scenarios it may still return with 0, check javadoc
                    continue;
                }

                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();

                    if (key.isReadable()) {
                        SocketChannel socket = (SocketChannel) key.channel();

                        buffer.clear(); // switch to writing mode
                        int r = socket.read(buffer);
                        if (r < 0) {
                            socket.close();
                            continue;
                        }

                        buffer.flip(); // switch to reading mode

                        String input = new String(buffer.array(), 0, buffer.limit());
                        input = input.trim();
                        String[] words = input.split("\\s+");
                        String command = words[0];
                        String[] arguments = Arrays.copyOfRange(words,1, words.length);

                        String replay;
                        switch (command) {
                            case REGISTER_COMMAND -> replay =  register(socket, arguments);
                            case LOGIN_COMMAND -> replay = login(socket, arguments);
                            case LOGOUT_COMMAND -> replay = logout(socket);
                            case POST_WISH_COMMAND -> replay = postWish(socket, arguments);
                            case GET_WISH_COMMAND -> replay = getWish(socket);
                            case DISCONNECT_COMMAND -> replay = DISCONNECT_MESSAGE;
                            default -> replay = UNKNOWN_COMMAND_MESSAGE;
                        }
                        replay += System.lineSeparator();

                        buffer.clear(); // switch to writing mode
                        buffer.put(replay.getBytes()); //buffer fill

                        buffer.flip(); // switch to reading mode
                        socket.write(buffer);
                        buffer.clear();

                        if (input.equals(DISCONNECT_COMMAND)) {
                            break;
                        }
                    } else if (key.isAcceptable()) {
                        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
                        SocketChannel accept = sockChannel.accept();
                        accept.configureBlocking(false); // configure in non-blocking mode
                        accept.register(selector, SelectionKey.OP_READ);
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            for (SocketChannel client : clients.keySet()) {
                client.close();
            }
            selector.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String register(SocketChannel sc, String[] args) {
        if (clients.keySet().contains(sc)) {
            return MessageFormat.format(ALREADY_LOGGED_MESSAGE, clients.get(sc));
        }
        if (args.length < 2) {
            return PROVIDE_USERNAME_PASSWORD_MESSAGE;
        }
        String username = args[0];
        String password = args[1];
        if (registeredUsers.containsKey(username)) {
            return MessageFormat.format(DUPLICATE_USERNAME_MESSAGE, username);
        }
        if (!username.matches(USERNAME_PATTERN)) {
            return MessageFormat.format(INVALID_USERNAME_MESSAGE, username);
        }
        // Success
        registeredUsers.put(username, password);
        clients.put(sc, username);
        return MessageFormat.format(SUCCESSFUL_REGISTRATION_MESSAGE, username);
    }

    private String login(SocketChannel sc, String[] args) {
        if (clients.keySet().contains(sc)) {
            return MessageFormat.format(ALREADY_LOGGED_MESSAGE, clients.get(sc));
        }
        if (args.length < 2) {
            return PROVIDE_USERNAME_PASSWORD_MESSAGE;
        }
        String username = args[0];
        String password = args[1];
        if (!registeredUsers.containsKey(username) || !registeredUsers.get(username).equals(password)) {
            return INVALID_USERNAME_PASSWORD_MESSAGE;
        }
        // Success
        clients.put(sc, username);
        return MessageFormat.format(SUCCESSFUL_LOGIN_MESSAGE, username);
    }

    private String logout(SocketChannel sc) {
        if (!clients.keySet().contains(sc)) {
            return NOT_LOGGED_MESSAGE;
        }
        // Success
        clients.remove(sc);
        return SUCCESSFUL_LOGOUT_MESSAGE;
    }

    private String postWish(SocketChannel sc, String[] args) {
        if (!clients.keySet().contains(sc)) {
            return NOT_LOGGED_MESSAGE;
        }
        if (args.length < 2) {
            return PROVIDE_USERNAME_WISH_MESSAGE;
        }
        String username = args[0];
        String wish = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        List<String> wishesList;
        if (!registeredUsers.containsKey(username)) {
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
        usersWishLists.get(username).add(wish);
        return MessageFormat.format(SUCCESSFUL_SUBMITTED_GIFT_MESSAGE, wish, username);
    }

    private String getWish(SocketChannel sc) {
        if (!clients.keySet().contains(sc)) {
            return NOT_LOGGED_MESSAGE;
        }
        String loggedUsername = clients.get(sc);
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
        return MessageFormat.format(WISHLIST_MESSAGE, user, wishes);
    }
}
