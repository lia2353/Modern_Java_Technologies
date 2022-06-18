package bg.sofia.uni.fmi.mjt.splitwise.storage;

import bg.sofia.uni.fmi.mjt.splitwise.mjt.splitwise.storage.PaymentsLogData;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PaymentsLogDataTest {

    private static final boolean FRIEND_PAYMENT = false;
    private static final boolean GROUP_PAYMENT = true;
    private static final String TEST_USERNAME = "testUsername";
    private static final Double TEST_AMOUNT = 4.2;
    private static final String TEST_GROUP_NAME = "testGroupName";
    private static final String TEST_FRIEND_NAME = "testFriendName";
    private static final String TEST_REASON = "testReason";
    private static final LocalDate TEST_DATE = LocalDate.of(2000, 1, 1);

    private static PaymentsLogData payments;

    @Before
    public void setUp() {
        payments = new PaymentsLogData();
    }

    // addPayment
    @Test(expected = IllegalArgumentException.class)
    public void testAddPaymentNullArguments() {
        payments.addPayment(true, null, null, null, null, null);
    }

    @Test
    public void testAddPaymentFriendPayment() {
        assertTrue(payments.hasNoPayments(TEST_USERNAME));
        payments.addPayment(FRIEND_PAYMENT, TEST_USERNAME, TEST_AMOUNT, TEST_FRIEND_NAME, TEST_REASON, TEST_DATE);
        assertFalse(payments.hasNoPayments(TEST_USERNAME));
    }

    @Test
    public void testAddPaymentGroupPayment() {
        assertTrue(payments.hasNoPayments(TEST_USERNAME));
        payments.addPayment(GROUP_PAYMENT, TEST_USERNAME, TEST_AMOUNT, TEST_GROUP_NAME, TEST_REASON, TEST_DATE);
        assertFalse(payments.hasNoPayments(TEST_USERNAME));
    }

    // getPayments
    @Test(expected = IllegalArgumentException.class)
    public void testGetPaymentsNullArgument() {
        payments.getPayments(null);
    }

    @Test
    public void tesGetPaymentsNoPayments() {
        assertTrue(payments.hasNoPayments(TEST_USERNAME));
        assertNull(payments.getPayments(TEST_USERNAME));
    }

    @Test
    public void tesGetPaymentsWithPayment() {
        assertTrue(payments.hasNoPayments(TEST_USERNAME));
        payments.addPayment(FRIEND_PAYMENT, TEST_USERNAME, TEST_AMOUNT, TEST_FRIEND_NAME, TEST_REASON, TEST_DATE);

        String expected = "Your payments: " + System.lineSeparator() + "Split " + TEST_AMOUNT + " LV expense with "
                + TEST_FRIEND_NAME + " on " + TEST_DATE + ". Reason for payment: " + TEST_REASON;

        assertEquals(expected, payments.getPayments(TEST_USERNAME));
    }

    // hasNoPayments
    @Test(expected = IllegalArgumentException.class)
    public void testHasNoPaymentsNullArgument() {
        payments.hasNoPayments(null);
    }

    @Test
    public void tesHasNoPaymentsNoPayments() {
        assertTrue(payments.hasNoPayments(TEST_USERNAME));
    }

    @Test
    public void tesHasNoPaymentsWithPayment() {
        assertTrue(payments.hasNoPayments(TEST_USERNAME));
        payments.addPayment(FRIEND_PAYMENT, TEST_USERNAME, TEST_AMOUNT, TEST_FRIEND_NAME, TEST_REASON, TEST_DATE);
        assertFalse(payments.hasNoPayments(TEST_USERNAME));
    }
}
