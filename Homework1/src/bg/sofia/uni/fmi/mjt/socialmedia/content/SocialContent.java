package bg.sofia.uni.fmi.mjt.socialmedia.content;

import java.time.LocalDateTime;

public interface SocialContent extends Content {
    void addLike();

    void addComment(String text, String commenterUsername);

    boolean isExpired();

    LocalDateTime getPublishedOn();
}
