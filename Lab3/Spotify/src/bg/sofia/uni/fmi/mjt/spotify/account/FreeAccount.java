package bg.sofia.uni.fmi.mjt.spotify.account;

import bg.sofia.uni.fmi.mjt.spotify.library.Library;

public class FreeAccount extends Account {

    private static final int NUMBER_OF_PLAYABLE_CONTENT_BEFORE_ADS = 5;

    public FreeAccount(String email, Library library) {
        super(email, library);
    }

    @Override
    public int getAdsListenedTo() {
        return super.getTotalListenedContent() / NUMBER_OF_PLAYABLE_CONTENT_BEFORE_ADS;
    }

    @Override
    public AccountType getType() {
        return AccountType.FREE;
    }

}
