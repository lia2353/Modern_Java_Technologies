package bg.sofia.uni.fmi.mjt.revolut.account;

public class EURAccount extends Account {
    private static final String EUR_CURRENCY = "EUR";

    public EURAccount(String IBAN, double amount) {
        super(IBAN, amount);
    }

    @Override
    public String getCurrency() {
        return EUR_CURRENCY;
    }
}
