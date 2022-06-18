package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class LoginData {
    private final Map<SocketChannel, String> loggedUsers;

    public LoginData() {
        this.loggedUsers = new HashMap<>();
    }

    public void loginUser(SocketChannel sc, String username) {
        if (sc == null || username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        loggedUsers.put(sc, username);
    }

    public boolean isLogged(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        return loggedUsers.containsKey(sc);
    }

    public boolean isUsernameLogged(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        return loggedUsers.containsValue(username);
    }

    public String getLoggedUsername(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        return loggedUsers.get(sc);
    }

    public void logoutUser(SocketChannel sc) {
        if (sc == null) {
            throw new IllegalArgumentException("Provided argument is null.");
        }
        loggedUsers.remove(sc);
    }
}
