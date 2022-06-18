package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.HashMap;
import java.util.Map;

public class RegistrationsData {
    private static final String USERNAME_PATTERN = "[a-zA-Z0-9_.-]+";

    private final Map<String, String> registeredUsers;

    public RegistrationsData() {
        this.registeredUsers = new HashMap<>();
    }

    public void addRegistration(String username, String password) {
        registeredUsers.put(username, password);
    }

    public boolean isRegistered(String username) {
        return registeredUsers.containsKey(username);
    }

    public boolean isValidUsername(String username) {
        return username.matches(USERNAME_PATTERN);
    }

    public boolean isAuthenticationSuccessful(String username, String password) {
        if (isRegistered(username)) {
            return registeredUsers.get(username).equals(password);
        }
        return false;
    }
}
