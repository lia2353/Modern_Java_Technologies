package bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise;

import java.util.Objects;

public class User {

    private final String username;
    private final String password; //here encode
    private final String email;

    private static final String DELIMITER = ", ";

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User(String userInfo) {
        this.username = userInfo.split(DELIMITER)[0];
        this.password = userInfo.split(DELIMITER)[1];
        this.email = userInfo.split(DELIMITER)[2];
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username + DELIMITER + password + DELIMITER + email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return username.equals(user.username) &&
                password.equals(user.password) &&
                email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }
}
