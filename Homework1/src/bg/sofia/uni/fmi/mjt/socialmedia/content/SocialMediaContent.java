package bg.sofia.uni.fmi.mjt.socialmedia.content;

import bg.sofia.uni.fmi.mjt.socialmedia.content.enums.ContentType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class SocialMediaContent implements SocialContent {
    private String id;
    private LocalDateTime publishedOn;
    private int numberOfLikes;
    private int numberOfComments;
    private String description;
    private Set<String> tags;
    private Set<String> mentions;
    private Set<Comment> comments;
    private boolean expired;

    public SocialMediaContent(String authorUsername, int currIdNumber, LocalDateTime publishedOn, String description) {
        this.id = authorUsername + "-" + currIdNumber;
        this.publishedOn = publishedOn;
        this.description = description;
        this.tags = new HashSet<>();
        this.mentions = new HashSet<>();
        separateTagsAndMentions();
        this.comments = new HashSet<>();
    }

    private void separateTagsAndMentions() {
        String[] words = description.split(" ");
        for (int i = 0; i < words.length; ++i) {
            if (words[i].startsWith("@")) {
                mentions.add(words[i]);
            }
            if (words[i].startsWith("#")) {
                tags.add(words[i]);
            }
        }
    }

    @Override
    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    @Override
    public int getNumberOfComments() {
        return numberOfComments;
    }

    @Override
    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Collection<String> getTags() {
        return tags;
    }

    @Override
    public Collection<String> getMentions() {
        return mentions;
    }

    public abstract ContentType getType();

    @Override
    public boolean isExpired() {
        if (!expired) {
            LocalDateTime dateToExpire = publishedOn.plusDays(getType().getActivePeriodDays());
            expired = LocalDateTime.now().isAfter(dateToExpire);
        }
        return expired;
    }

    @Override
    public void addLike() {
        ++numberOfLikes;
    }

    @Override
    public void addComment(String text, String commenterUsername) {
        comments.add(new Comment(commenterUsername, text));
        ++numberOfComments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SocialMediaContent)) {
            return false;
        }
        SocialMediaContent that = (SocialMediaContent) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
