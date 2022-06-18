package bg.sofia.uni.fmi.mjt.socialmedia.account;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Account implements Comparable<Account> {
    static final String dateTimePattern = "HH:mm:ss dd.mm.yy";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);

    private String username;
    private List<String> activityLog;
    private List<String> publishedContent;
    private int popularityRating;

    public Account(String username) {
        this.username = username;
        this.activityLog = new ArrayList<>();
        this.publishedContent = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public List<String> getActivityLog() {
        return activityLog;
    }

    public List<String> getPublishedContent() {
        return publishedContent;
    }

    public void incrementPopularityRating() {
        ++popularityRating;
    }

    public void addLikeLog(String id) {
        activityLog.add(LocalDateTime.now().format(formatter) + ": Liked content with id " + id);
    }

    public void addCommentLog(String text, String id) {
        activityLog.add(LocalDateTime.now().format(formatter) + ": Commented \"" + text
                + "\" on content with id " + id);
    }

    public void addPostLog(String id) {
        publishedContent.add(id);
        activityLog.add(LocalDateTime.now().format(formatter) + ": Created a post with id " + id);
    }

    public void addStoryLog(String id) {
        publishedContent.add(id);
        activityLog.add(LocalDateTime.now().format(formatter) + ": Created a story with id " + id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Account)) {
            return false;
        }
        Account account = (Account) o;
        return username.equals(account.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public int compareTo(Account other) {
        return (other.popularityRating - this.popularityRating);
    }
}
