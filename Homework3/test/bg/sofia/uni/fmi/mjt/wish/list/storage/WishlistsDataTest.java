package bg.sofia.uni.fmi.mjt.wish.list.storage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WishlistsDataTest {
    private static final String testUsername = "testUsername";
    private static final String anotherTestUsername = "testUsername2";
    private static final String testWish = "testWish";
    private static final String anotherTestWish = "test Wish 2";

    private static WishlistsData storage;

    @Before
    public void setUp() {
        storage = new WishlistsData();
    }

    @Test
    public void testAddWishOneWish() {
        storage.addWish(testUsername, testWish);

        String expected = testWish;
        String actual = storage.getWishlist(testUsername);

        assertFalse(storage.isEmpty());
        assertEquals(expected.length(), actual.length());
        assertEquals(expected, actual);
    }

    @Test
    public void testAddWishTwoWishes() {
        storage.addWish(testUsername, testWish);
        storage.addWish(testUsername, anotherTestWish);

        String expected = testWish + ", " + anotherTestWish;
        String actual = storage.getWishlist(testUsername);

        assertFalse(storage.isEmpty());
        assertEquals(expected.length(), actual.length());
        assertEquals(expected, actual);
    }

    @Test
    public void testIsEmptyNoWishes() {
        assertTrue(storage.isEmpty());
    }

    @Test
    public void testIsEmptyContainsWish() {
        storage.addWish(testUsername, testWish);

        assertFalse(storage.isEmpty());
    }

    @Test
    public void testIsEmptyUsersWishListEmptyStorage() {
        assertTrue(storage.isEmptyUsersWishList(testUsername));
    }

    @Test
    public void testIsEmptyUsersWishListNoWishesForUser() {
        storage.addWish(testUsername, testWish);

        assertTrue(storage.isEmptyUsersWishList(anotherTestUsername));
    }

    @Test
    public void testIsEmptyUsersWishListNotEmpty() {
        storage.addWish(testUsername, testWish);

        assertFalse(storage.isEmptyUsersWishList(testUsername));
    }

    @Test
    public void testAlreadyContainsWishEmptyStorage() {
        assertFalse(storage.alreadyContainsWish(testUsername, testWish));
    }

    @Test
    public void testAlreadyContainsWishEmptyUsersWishList() {
        storage.addWish(testUsername, testWish);

        assertFalse(storage.alreadyContainsWish(anotherTestUsername, testWish));
    }

    @Test
    public void testAlreadyContainsWishNoSuchWishInUsersWishlist() {
        storage.addWish(testUsername, testWish);

        assertFalse(storage.alreadyContainsWish(testUsername, anotherTestWish));
    }

    @Test
    public void testAlreadyContainsWishUsersWishlistContainsWish() {
        storage.addWish(testUsername, testWish);

        assertTrue(storage.alreadyContainsWish(testUsername, testWish));
    }

    @Test
    public void testContainsOnlyUsersWishlistEmptySorage() {
        assertFalse(storage.containsOnlyUsersWishlist(testUsername));
    }

    @Test
    public void testContainsOnlyUsersWishlistNotOnlyUsersWishlist() {
        storage.addWish(testUsername, testWish);
        storage.addWish(anotherTestUsername, testWish);

        assertFalse(storage.containsOnlyUsersWishlist(testUsername));
    }

    @Test
    public void testContainsOnlyUsersWishlistOnlyUsersWishlist() {
        storage.addWish(testUsername, testWish);

        assertTrue(storage.containsOnlyUsersWishlist(testUsername));
    }

    @Test
    public void testGetRandomUsernameWithWishList() {
        storage.addWish(testUsername, testWish);
        storage.addWish(anotherTestUsername, anotherTestWish);

        assertEquals(anotherTestUsername, storage.getRandomUsernameWithWishList(testUsername));
    }

    @Test
    public void testGetWishlistOneWish() {
        storage.addWish(testUsername, testWish);

        String expected = testWish;
        String actual = storage.getWishlist(testUsername);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetWishlistTwoWishes() {
        storage.addWish(testUsername, testWish);
        storage.addWish(testUsername, anotherTestWish);

        String expected = testWish + ", " + anotherTestWish;
        String actual = storage.getWishlist(testUsername);

        assertEquals(expected, actual);
    }

    @Test
    public void testRemoveWishlistUserWithNoWishlist() {
        assertTrue(storage.isEmptyUsersWishList(testUsername));

        storage.removeWishlist(testUsername);

        assertTrue(storage.isEmptyUsersWishList(testUsername));
    }

    @Test
    public void testRemoveWishlistWithUserWishList() {
        storage.addWish(testUsername, testWish);
        assertFalse(storage.isEmptyUsersWishList(testUsername));

        storage.removeWishlist(testUsername);

        assertTrue(storage.isEmptyUsersWishList(testUsername));
    }
}
