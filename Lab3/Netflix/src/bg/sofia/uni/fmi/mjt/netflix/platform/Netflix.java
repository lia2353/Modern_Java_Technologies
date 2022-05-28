package bg.sofia.uni.fmi.mjt.netflix.platform;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.Streamable;
import bg.sofia.uni.fmi.mjt.netflix.content.MeteredStreamableContent;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Arrays;

public class Netflix implements StreamingService {
    public static final int INVALID_INDEX = -1;

    private final Account[] accounts;
    private final MeteredStreamableContent[] videoContent;
    private MeteredStreamableContent mostWatchedContent;
    private int totalWatchedTimeByUsers;

    public Netflix(Account[] accounts, Streamable[] streamableContent) {
        this.accounts = accounts;
        this.videoContent = new MeteredStreamableContent[streamableContent.length];
        for (int i = 0; i < videoContent.length; i++) {
            videoContent[i] = new MeteredStreamableContent(streamableContent[i]);
        }
    }

    @Override
    public void watch(Account user, String videoContentName) throws ContentUnavailableException {
        if (!isUserRegistered(user)) {
            throw new UserNotFoundException(String.format("User %s is not registered in the streaming platform", user.username()));
        }

        MeteredStreamableContent videoContent = findMeteredStreamableContentByName(videoContentName);
        if (videoContent == null) {
            throw new ContentNotFoundException(String.format("The %s content is not present in the streaming platform.", videoContentName));
        }

        if (isUserAgeRestricted(user, videoContent.getContent())) {
            throw new ContentUnavailableException(String.format("The %s content is age restricted and the user %s is not permitted to access it.", videoContentName, user.username()));
        }

        videoContent.incrementWatchedTime();

        totalWatchedTimeByUsers += videoContent.getContent().getDuration();

        if (mostWatchedContent == null || videoContent.getTotalTimesWatched() > mostWatchedContent.getTotalTimesWatched()) {
            mostWatchedContent = videoContent;
        }
    }

    @Override
    public Streamable findByName(String videoContentName) {
        MeteredStreamableContent content = findMeteredStreamableContentByName(videoContentName);
        return content != null ? content.getContent() : null;
    }

    @Override
    public Streamable mostViewed() {
        return mostWatchedContent != null ? mostWatchedContent.getContent() : null;
    }

    @Override
    public int totalWatchedTimeByUsers() {
        return totalWatchedTimeByUsers;
    }

    private boolean isUserRegistered(Account account) {
        for (Account value : accounts) {
            if (value.equals(account)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUserAgeRestricted(Account user, Streamable content) {
        int age = LocalDateTime.now().getYear() - user.birthdayDate().getYear();
        boolean isRestricted =
                switch (content.getRating()) {
                    case PG13 -> age <= 13;
                    case NC17 -> age <= 17;
                    case G -> false;
                };
        return isRestricted;
    }

    private MeteredStreamableContent findMeteredStreamableContentByName(String videoContentName) {
        for (MeteredStreamableContent video : videoContent) {
            if (video.getContent().getTitle().equalsIgnoreCase(videoContentName.strip())) {
                return video;
            }
        }
        return null;
    }

}

