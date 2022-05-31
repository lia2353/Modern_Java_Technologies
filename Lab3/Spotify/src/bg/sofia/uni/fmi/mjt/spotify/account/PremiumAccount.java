package bg.sofia.uni.fmi.mjt.spotify.account;

import bg.sofia.uni.fmi.mjt.spotify.library.Library;

public class PremiumAccount extends Account {

    private static final int NUMBER_OF_ADS = 0;

    public PremiumAccount(String email, Library library) {
        super(email, library);
    }

    @Override
    public int getAdsListenedTo() {
        return NUMBER_OF_ADS;
    }

    @Override
    public AccountType getType() {
        return AccountType.PREMIUM;
    }

}
