package bg.sofia.uni.fmi.mjt.wish.list.storage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegistrationsDataTest {
    private static final String testUsername = "testUsername";
    private static final String invalidUsername = "u$ername";
    private static final String testPassword = "testPassword";

    private static RegistrationsData storage;

    @Before
    public void setUp() {
        storage = new RegistrationsData();
    }

    @Test
    public void testAddRegistration() {
        storage.addRegistration(testUsername, testPassword);

        assertTrue(storage.isRegistered(testUsername));
    }

    @Test
    public void testIsRegisteredUserNotRegistered() {
        assertFalse(storage.isRegistered(testUsername));
    }

    @Test
    public void testIsRegisteredUserIsRegistered() {
        storage.addRegistration(testUsername, testPassword);

        assertTrue(storage.isRegistered(testUsername));
    }

    @Test
    public void testIsValidUsernameValidUsername() {
        assertTrue(storage.isValidUsername(testUsername));
    }

    @Test
    public void testIsValidUsernameNotValidUsername() {
        assertFalse(storage.isValidUsername(invalidUsername));
    }

    @Test
    public void testIsAuthenticationSuccessfulValidInput() {
        storage.addRegistration(testUsername, testPassword);

        assertTrue(storage.isAuthenticationSuccessful(testUsername, testPassword));
    }

    @Test
    public void testIsAuthenticationSuccessfulNotRegistered() {
        assertFalse(storage.isAuthenticationSuccessful(testUsername, testPassword));
    }

    @Test
    public void testIsAuthenticationSuccessfulRegisteredButInvalidPassword() {
        assertFalse(storage.isAuthenticationSuccessful(testUsername, testUsername));
    }
}
