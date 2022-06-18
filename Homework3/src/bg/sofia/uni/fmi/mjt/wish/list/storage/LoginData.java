package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class LoginData {
    private final Map<SocketChannel, String> loggedUsers;

    public LoginData() {
        this.loggedUsers = new HashMap<>();
    }

    public void loginUser(SocketChannel sc, String username) {
        loggedUsers.put(sc, username);
    }

    public boolean isLogged(SocketChannel sc) {
        return loggedUsers.containsKey(sc);
    }

    public String getLoggedUsername(SocketChannel sc) {
        return loggedUsers.get(sc);
    }

    public void logoutUser(SocketChannel sc) {
        loggedUsers.remove(sc);
    }
}
