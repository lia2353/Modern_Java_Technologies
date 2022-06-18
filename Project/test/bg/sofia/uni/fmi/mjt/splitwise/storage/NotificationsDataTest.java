package bg.sofia.uni.fmi.mjt.splitwise.storage;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.NotificationsData;
import org.junit.Before;
import org.junit.Test;

import java.text.MessageFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotificationsDataTest {

    private static final String TEST_USERNAME = "testUsername";
    private static final Double TEST_AMOUNT = 4.2;
    private static final String TEST_GROUP_NAME = "testGroupName";
    private static final String TEST_FRIEND_NAME = "testFriendName";
    private static final String TEST_REASON = "testReason";

    private static final String NO_NOTIFICATIONS_MESSAGE = "No notifications to show.";
    private static final String NOTIFICATIONS = "*** Notifications ***";
    private static final String FRIENDS = "Friends: ";
    private static final String GROUPS = "Groups: ";
    private static final String OWE_NOTIFICATION = "You owe {0} {1} LV [{2}].";
    private static final String APPROVED_PAYMENT_NOTIFICATION = "{0} approved your payment {1} LV.";

    private static NotificationsData notifications;

    @Before
    public void setUp() {
        notifications = new NotificationsData();
    }

    // addPayment
    @Test(expected = IllegalArgumentException.class)
    public void testAddPaymentNullArguments() {
        notifications.addPayment(null, null, null, null, TEST_GROUP_NAME);
    }

    @Test
    public void testAddPaymentFriendPayment() {
        String expected = NOTIFICATIONS + System.lineSeparator() + FRIENDS + System.lineSeparator()
                + MessageFormat.format(OWE_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_REASON);

        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        notifications.addPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_REASON, null);
        assertTrue(notifications.hasFriendsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    @Test
    public void testAddPaymentGroupPayment() {
        String expected = NOTIFICATIONS + System.lineSeparator() + GROUPS + System.lineSeparator()
                + "* " + TEST_GROUP_NAME + System.lineSeparator()
                + MessageFormat.format(OWE_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_REASON);

        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        notifications.addPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_REASON, TEST_GROUP_NAME);
        assertTrue(notifications.hasGroupsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    // addApprovedPayment
    @Test(expected = IllegalArgumentException.class)
    public void testAddApprovedPaymentNullArguments() {
        notifications.addApprovedPayment(null, null, null, TEST_GROUP_NAME);
    }

    @Test
    public void testAddApprovedPaymentFriendPayment() {
        String expected = NOTIFICATIONS + System.lineSeparator() + FRIENDS + System.lineSeparator()
                + MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT);

        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        assertTrue(notifications.hasFriendsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    @Test
    public void testAddApprovedPaymentGroupPayment() {
        String expected = NOTIFICATIONS + System.lineSeparator() + GROUPS + System.lineSeparator()
                + "* " + TEST_GROUP_NAME + System.lineSeparator()
                + MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT);

        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertTrue(notifications.hasGroupsNotifications(TEST_USERNAME));
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    // hasNoNotifications
    @Test
    public void testHasNoNotificationsWithNoNotificationsAvailable() {
        assertTrue(notifications.hasNoNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasNoNotificationsWithFriendsNotificationsAvailable() {
        assertTrue(notifications.hasNoNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        assertFalse(notifications.hasNoNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasNoNotificationsWithGroupsNotificationsAvailable() {
        assertTrue(notifications.hasNoNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertFalse(notifications.hasNoNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasNoNotificationsWithFriendsAndGroupsNotificationsAvailable() {
        assertTrue(notifications.hasNoNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertFalse(notifications.hasNoNotifications(TEST_USERNAME));
    }

    // hasFriendsNotifications
    @Test
    public void testHasFriendsNotificationsWithNoNotificationsAvailable() {
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasFriendsNotificationsWithFriendsNotificationsAvailable() {
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        assertTrue(notifications.hasFriendsNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasFriendsNotificationsWithGroupsNotificationsAvailable() {
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasFriendsNotificationsWithFriendsAndGroupsNotificationsAvailable() {
        assertFalse(notifications.hasFriendsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertTrue(notifications.hasFriendsNotifications(TEST_USERNAME));
    }

    // hasGroupsNotifications
    @Test
    public void testHasGroupsNotificationsWithNoNotificationsAvailable() {
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasGroupsNotificationsWithFriendsNotificationsAvailable() {
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasGroupsNotificationsWithGroupsNotificationsAvailable() {
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertTrue(notifications.hasGroupsNotifications(TEST_USERNAME));
    }

    @Test
    public void testHasGroupsNotificationsWithFriendsAndGroupsNotificationsAvailable() {
        assertFalse(notifications.hasGroupsNotifications(TEST_USERNAME));
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertTrue(notifications.hasGroupsNotifications(TEST_USERNAME));
    }

    // getNotifications
    @Test
    public void testGetNotificationsWithNoNotificationsAvailable() {
        String expected = NOTIFICATIONS + System.lineSeparator() + NO_NOTIFICATIONS_MESSAGE;
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    @Test
    public void testGetNotificationsWithFriendsNotificationsAvailable() {
        String expected = NOTIFICATIONS + System.lineSeparator() + FRIENDS + System.lineSeparator()
                + MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT);

        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    @Test
    public void testGetNotificationsWithGroupsNotificationsAvailable() {
        String expected = NOTIFICATIONS + System.lineSeparator() + GROUPS + System.lineSeparator()
                + "* " + TEST_GROUP_NAME + System.lineSeparator()
                + MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT);

        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }

    @Test
    public void testGetNotificationsWithFriendsAndGroupsNotificationsAvailable() {
        String expected = NOTIFICATIONS + System.lineSeparator()
                + FRIENDS + System.lineSeparator()
                + MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT)
                + System.lineSeparator() + GROUPS + System.lineSeparator()
                + "* " + TEST_GROUP_NAME + System.lineSeparator()
                + MessageFormat.format(APPROVED_PAYMENT_NOTIFICATION, TEST_FRIEND_NAME, TEST_AMOUNT);

        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, null);
        notifications.addApprovedPayment(TEST_USERNAME, TEST_FRIEND_NAME, TEST_AMOUNT, TEST_GROUP_NAME);
        assertEquals(expected, notifications.getNotifications(TEST_USERNAME));
    }


}
