package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RegistrationsData {
    private static final String USERNAME_PATTERN = "[a-zA-Z0-9_.]{3,}+";
    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String REGISTERED_USERS_FILE_PATH = "./resources/registeredUsers.txt";

    private final Map<String, User> registeredAccounts;

    public RegistrationsData() {
        registeredAccounts = new HashMap<>();
        initRegistrationsData();
    }

    public void initRegistrationsData() {
        Path file = Path.of(REGISTERED_USERS_FILE_PATH);
        if (Files.exists(file)) {
            try (BufferedReader br = Files.newBufferedReader(file)) {
                String line;
                while ((line = br.readLine()) != null) {
                    User user = new User(line);
                    registeredAccounts.put(user.getUsername(), user);
                }
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while reading from the file", e);
            }
        }
    }

    public synchronized void writeNewRegistrationData(User user) {
        if (!registeredAccounts.isEmpty()) {
            try (BufferedWriter fw = new BufferedWriter(new FileWriter(REGISTERED_USERS_FILE_PATH, true))) {
                fw.write(user.toString() + System.lineSeparator());
                fw.flush();
            } catch (IOException e) {
                throw new IllegalStateException("A problem occurred while writing in the file", e);
            }
        }
    }

    public void addRegistration(String username, String password, String email) {
        User newUser = new User(username, password, email);
        registeredAccounts.put(username, newUser);
        writeNewRegistrationData(newUser);
    }

    public boolean isRegistered(String username) {
        return registeredAccounts.containsKey(username);
    }

    public boolean isEmailTaken(String username, String email) {
        return isRegistered(username) && registeredAccounts.get(username).getEmail().equals(email);
    }

    public boolean isValidUsername(String username) {
        return username.matches(USERNAME_PATTERN);
    }

    public boolean isValidPassword(String password) {
        return password.matches(PASSWORD_PATTERN);
    }

    public boolean isValidEmail(String email) {
        return email.matches(EMAIL_PATTERN);
    }

    public boolean isAuthenticationSuccessful(String username, String password) {
        if (isRegistered(username)) {
            return registeredAccounts.get(username).getPassword().equals(password);
        }
        return false;
    }

}
