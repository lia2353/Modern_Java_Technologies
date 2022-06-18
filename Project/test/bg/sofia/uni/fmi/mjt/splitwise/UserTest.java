package bg.sofia.uni.fmi.mjt.splitwise;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password#";
    private static final String EMAIL = "user@email.com";

    private static final String DELIMITER = ", ";

    // getUsername
    @Test
    public void testGetUsernameInitWithArguments() {
        User user = new User(USERNAME, PASSWORD, EMAIL);
        assertEquals("Unexpected username returned", USERNAME, user.getUsername());
    }

    @Test
    public void testGetUsernameInitWithSting() {
        String userInfo = USERNAME + DELIMITER + PASSWORD + DELIMITER + EMAIL;
        User user = new User(userInfo);
        assertEquals("Unexpected username returned", USERNAME, user.getUsername());
    }

    // getEmail
    @Test
    public void testGetEmailInitWithArguments() {
        User user = new User(USERNAME, PASSWORD, EMAIL);
        assertEquals("Unexpected email returned", EMAIL, user.getEmail());
    }

    @Test
    public void testGetEmailInitWithSting() {
        String userInfo = USERNAME + DELIMITER + PASSWORD + DELIMITER + EMAIL;
        User user = new User(userInfo);
        assertEquals("Unexpected email returned", EMAIL, user.getEmail());
    }

    // getPassword
    @Test
    public void testGetPasswordInitWithArguments() {
        User user = new User(USERNAME, PASSWORD, EMAIL);
        assertEquals("Unexpected password returned", PASSWORD, user.getPassword());
    }

    @Test
    public void testGetPasswordInitWithSting() {
        String userInfo = USERNAME + DELIMITER + PASSWORD + DELIMITER + EMAIL;
        User user = new User(userInfo);
        assertEquals("Unexpected password returned", PASSWORD, user.getPassword());
    }

    // toString
    @Test
    public void testToStringInitWithArguments() {
        User user = new User(USERNAME, PASSWORD, EMAIL);
        String expected = USERNAME + DELIMITER + PASSWORD + DELIMITER + EMAIL;
        assertEquals("Unexpected string representation of user returned", expected, user.toString());
    }

    @Test
    public void testToStringInitWithSting() {
        String userInfo = USERNAME + DELIMITER + PASSWORD + DELIMITER + EMAIL;
        User user = new User(userInfo);
        assertEquals("Unexpected string representation of user returned", userInfo, user.toString());
    }
}
