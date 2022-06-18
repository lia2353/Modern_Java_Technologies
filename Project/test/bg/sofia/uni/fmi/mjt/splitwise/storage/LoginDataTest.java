package bg.sofia.uni.fmi.mjt.splitwise.storage;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.LoginData;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LoginDataTest {

    private static final String TEST_USERNAME = "testUsername";

    private static SocketChannel testSocket;
    private static LoginData logins;

    @Before
    public void setUp() {
        testSocket = mock(SocketChannel.class);
        logins = new LoginData();
    }

    // loginUser
    @Test(expected = IllegalArgumentException.class)
    public void testLoginUserNullSocket() {
        logins.loginUser(null, TEST_USERNAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginUserNullUsername() {
        logins.loginUser(testSocket, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoginUserNullArguments() {
        logins.loginUser(null, null);
    }

    @Test
    public void testLoginUserOneUser() {
        logins.loginUser(testSocket, TEST_USERNAME);

        assertTrue(logins.isLogged(testSocket));
        assertTrue(logins.isUsernameLogged(TEST_USERNAME));
    }

    // isLogged
    @Test(expected = IllegalArgumentException.class)
    public void testIsLoggedNullSocket() {
        logins.isLogged(null);
    }

    @Test
    public void testIsLoggedNotLogged() {
        assertFalse(logins.isLogged(testSocket));
    }

    @Test
    public void testIsLoggedIsLogged() {
        logins.loginUser(testSocket, TEST_USERNAME);
        assertTrue(logins.isLogged(testSocket));
    }

    // isUsernameLogged
    @Test(expected = IllegalArgumentException.class)
    public void testIsUsernameLoggedNullUsername() {
        logins.isUsernameLogged(null);
    }

    @Test
    public void testIsUsernameLoggedNotLogged() {
        assertFalse(logins.isUsernameLogged(TEST_USERNAME));
    }

    @Test
    public void testIsUsernameLoggedIsLogged() {
        logins.loginUser(testSocket, TEST_USERNAME);
        assertTrue(logins.isUsernameLogged(TEST_USERNAME));
    }

    // getLoggedUsername
    @Test(expected = IllegalArgumentException.class)
    public void testGetLoggedUsernameNullSocket() {
        logins.getLoggedUsername(null);
    }

    @Test
    public void testGetLoggedUsernameNotLogged() {
        assertNull(logins.getLoggedUsername(testSocket));
    }

    @Test
    public void testGetLoggedUsernameIsLogged() {
        logins.loginUser(testSocket, TEST_USERNAME);
        assertEquals(TEST_USERNAME, logins.getLoggedUsername(testSocket));
    }

    // logoutUser
    @Test(expected = IllegalArgumentException.class)
    public void testLogoutUserNullSocket() {
        logins.logoutUser(null);
    }

    @Test
    public void testLogoutUserNotLogged() {
        assertFalse(logins.isLogged(testSocket));
        logins.logoutUser(testSocket);
        assertFalse(logins.isLogged(testSocket));
    }

    @Test
    public void testLogoutUserIsLogged() {
        logins.loginUser(testSocket, TEST_USERNAME);
        assertTrue(logins.isLogged(testSocket));
        logins.logoutUser(testSocket);
        assertFalse(logins.isLogged(testSocket));
    }


}
