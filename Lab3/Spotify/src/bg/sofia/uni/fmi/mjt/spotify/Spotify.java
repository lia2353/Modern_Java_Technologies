package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.account.Account;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlayableNotFoundException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.PlaylistCapacityExceededException;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.StreamingServiceException;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;

import java.util.Arrays;

public class Spotify implements StreamingService {

    private static final double FREE_ACCOUNT_FEE = 0.1;
    private static final double Premium_ACCOUNT_FEE = 25.0;

    private Account[] registeredAccounts;
    private Playable[] playableContent;

    public Spotify(Account[] accounts, Playable[] playableContent) {
        this.registeredAccounts = Arrays.copyOf(accounts, accounts.length);
        this.playableContent = Arrays.copyOf(playableContent, playableContent.length);
    }

    @Override
    public void play(Account account, String title) throws AccountNotFoundException, PlayableNotFoundException {
        if (account == null) {
            throw new IllegalArgumentException("Account can not be null");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title can not be null or empty");
        }
        if (!isAccountRegistered(account)) {
            throw new AccountNotFoundException(String.format("Account with email %s is not registered in the platform", account.getEmail()));
        }

        account.listen(findByTitle(title));
    }

    @Override
    public void like(Account account, String title) throws AccountNotFoundException, PlayableNotFoundException, StreamingServiceException {
        if (account == null) {
            throw new IllegalArgumentException("Account can not be null");
        }
        if (!isAccountRegistered(account)) {
            throw new AccountNotFoundException(String.format("Account with email %s is not registered in the platform", account.getEmail()));
        }

        try {
            account.getLibrary().getLiked().add(findByTitle(title));
        } catch (PlaylistCapacityExceededException e) {
            throw new StreamingServiceException(String.format("Failed to add %s to the \"Liked Content\" default playlist", title), e);
        }
    }

    @Override
    public Playable findByTitle(String title) throws PlayableNotFoundException {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title can not be null or empty");
        }

        for (Playable content : playableContent) {
            if (content.getTitle().equals(title)) {
                return content;
            }
        }
        throw new PlayableNotFoundException(String.format("Content with title %s can not be found", title));
    }

    @Override
    public Playable getMostPlayed() {
        Playable mostPlayedContent = null;
        int mostPlays = 0;
        for (Playable content : playableContent) {
            if (content.getTotalPlays() >= mostPlays) {
                mostPlayedContent = content;
                mostPlays = content.getTotalPlays();
            }
        }
        return mostPlayedContent;
    }

    @Override
    public double getTotalListenTime() {
        double totalListenedTime = 0.0;
        for (Account account : registeredAccounts) {
            totalListenedTime += account.getTotalListenTime();
        }
        return totalListenedTime;
    }

    @Override
    public double getTotalPlatformRevenue() {
        double totalRevenue = 0.0;
        for (Account account : registeredAccounts) {
            totalRevenue +=
                    switch (account.getType()) {
                        case FREE -> FREE_ACCOUNT_FEE * account.getAdsListenedTo();
                        case PREMIUM -> Premium_ACCOUNT_FEE;
                    };
        }
        return totalRevenue;
    }

    private boolean isAccountRegistered(Account account) {
        for (Account registeredAccount : registeredAccounts) {
            if (registeredAccount.equals(account)) {
                return true;
            }
        }
        return false;
    }

}
