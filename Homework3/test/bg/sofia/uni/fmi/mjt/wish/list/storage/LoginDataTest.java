package bg.sofia.uni.fmi.mjt.wish.list.storage;

import org.junit.Before;
import org.junit.Test;

import java.nio.channels.SocketChannel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class LoginDataTest {
    private static final String testUsername = "testUsername";

    private static LoginData storage;
    private static SocketChannel testSocket;

    @Before
    public void setUp() {
        storage = new LoginData();
        testSocket = mock(SocketChannel.class);
    }

    @Test
    public void testLoginUser() {
        storage.loginUser(testSocket, testUsername);

        assertTrue(storage.isLogged(testSocket));
        assertEquals(testUsername, storage.getLoggedUsername(testSocket));
    }

    @Test
    public void testIsLoggedNotLoggedUserNotLogged() {
        assertFalse(storage.isLogged(testSocket));
    }

    @Test
    public void testIsLoggedNotLoggedUserIsLogged() {
        storage.loginUser(testSocket, testUsername);

        assertTrue(storage.isLogged(testSocket));
    }

    @Test
    public void testGetLoggedUsernameUserNotLogged() {
        assertNotEquals(testUsername, storage.getLoggedUsername(testSocket));
    }

    @Test
    public void testGetLoggedUsernameUserIsLogged() {
        storage.loginUser(testSocket, testUsername);

        assertEquals(testUsername, storage.getLoggedUsername(testSocket));
    }

    @Test
    public void testLogoutUserUserIsLogged() {
        storage.loginUser(testSocket, testUsername);
        assertTrue(storage.isLogged(testSocket));

        storage.logoutUser(testSocket);
        assertFalse(storage.isLogged(testSocket));
    }

    @Test
    public void testLogoutUserUserNotLogged() {
        assertFalse(storage.isLogged(testSocket));

        storage.logoutUser(testSocket);
        assertFalse(storage.isLogged(testSocket));
    }

}
