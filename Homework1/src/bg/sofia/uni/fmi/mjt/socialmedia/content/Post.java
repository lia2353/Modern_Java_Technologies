package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.content.enums.ContentType;

import java.time.LocalDateTime;

public class Post extends SocialMediaContent {
    public Post(String authorUsername, int currIdNumber, LocalDateTime publishedOn, String description) {
        super(authorUsername, currIdNumber, publishedOn, description);
    }

    public ContentType getType() {
        return ContentType.POST;
    }
}
