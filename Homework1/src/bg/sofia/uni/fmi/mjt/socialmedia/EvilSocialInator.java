package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.account.Account;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Post;
import bg.sofia.uni.fmi.mjt.socialmedia.content.SocialContent;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Story;
import bg.sofia.uni.fmi.mjt.socialmedia.content.enums.ContentType;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.ContentNotFoundException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.NoUsersException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.socialmedia.exceptions.UsernameNotFoundException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class EvilSocialInator implements SocialMediaInator {
    private final Map<String, Account> accountsRegistry;
    private final Map<String, SocialContent> contentRegistry;
    private int contentIndex;

    public EvilSocialInator() {
        this.accountsRegistry = new HashMap<>();
        this.contentRegistry = new HashMap<>();
    }

    @Override
    public void register(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        if (accountsRegistry.containsKey(username)) {
            throw new UsernameAlreadyExistsException("There is already a user with username " + username
                    + "registered in the platform.");
        }
        accountsRegistry.put(username, new Account(username));
    }

    @Override
    public String publishPost(String username, LocalDateTime publishedOn, String description) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        if (publishedOn == null) {
            throw new IllegalArgumentException("Provided date is null.");
        }
        if (description == null) {
            throw new IllegalArgumentException("Provided description is null.");
        }

        Account user = accountsRegistry.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " does not exist in the platform.");
        }

        SocialContent newContent = new Post(username, contentIndex, publishedOn, description);
        estimatePopularityRatings(newContent.getMentions());

        contentRegistry.put(newContent.getId(), newContent);
        ++contentIndex;
        user.addPostLog(newContent.getId());

        return newContent.getId();
    }

    @Override
    public String publishStory(String username, LocalDateTime publishedOn, String description) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        if (publishedOn == null) {
            throw new IllegalArgumentException("Provided date is null.");
        }
        if (description == null) {
            throw new IllegalArgumentException("Provided description is null.");
        }

        Account user = accountsRegistry.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " does not exist in the platform.");
        }

        SocialContent newContent = new Story(username, contentIndex, publishedOn, description);
        estimatePopularityRatings(newContent.getMentions());

        contentRegistry.put(newContent.getId(), newContent);
        ++contentIndex;
        user.addStoryLog(newContent.getId());

        return newContent.getId();
    }

    private void estimatePopularityRatings(Collection<String> mentions) {
        for (String mention : mentions) {
            Account account = accountsRegistry.get(mention.substring(1)); //removes leading @
            if (account != null) {
                account.incrementPopularityRating();
            }
        }
    }

    @Override
    public void like(String username, String id) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        if (id == null) {
            throw new IllegalArgumentException("Provided id is null.");
        }

        Account user = accountsRegistry.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " does not exist in the platform.");
        }
        SocialContent content = contentRegistry.get(id);
        if (content == null) {
            throw new ContentNotFoundException("There is no content with id " + id + " in the platform.");
        }

        content.addLike();
        user.addLikeLog(id);
    }

    @Override
    public void comment(String username, String text, String id) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        if (text == null) {
            throw new IllegalArgumentException("Provided text is null.");
        }
        if (id == null) {
            throw new IllegalArgumentException("Provided id is null.");
        }

        Account user = accountsRegistry.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " does not exist in the platform.");
        }
        SocialContent content = contentRegistry.get(id);
        if (content == null) {
            throw new ContentNotFoundException("There is no content with id " + id + " in the platform.");
        }

        content.addComment(text, username);
        user.addCommentLog(text, id);
    }

    @Override
    public Collection<Content> getNMostPopularContent(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative number as argument");
        }

        Set<Content> result = new TreeSet<>(contentPopularityComparator);
        for (SocialContent currentContent : contentRegistry.values()) {
            if (!currentContent.isExpired()) {
                result.add(currentContent);
            }
        }
        
        if (result.size() > n) {
            return Collections.unmodifiableCollection(new ArrayList<>(result).subList(0, n));
        }
        return Collections.unmodifiableCollection(result);
    }

    public static Comparator<Content> contentPopularityComparator = new Comparator<>() {
        @Override
        public int compare(Content first, Content second) {
            return second.getNumberOfComments() + second.getNumberOfLikes()
                    - first.getNumberOfComments() - first.getNumberOfLikes();
        }
    };

    @Override
    public Collection<Content> getNMostRecentContent(String username, int n) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Negative number as argument");
        }

        Account user = accountsRegistry.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " does not exist in the platform.");
        }

        Set<SocialContent> result = new TreeSet<>(contentMostRecentComparator);
        for (String contentId : user.getPublishedContent()) {
            if (!contentRegistry.get(contentId).isExpired()) {
                result.add(contentRegistry.get(contentId));
            }
        }

        if (result.size() > n) {
            return Collections.unmodifiableCollection(new ArrayList<>(result).subList(0, n));
        }
        return Collections.unmodifiableCollection(result);
    }

    public static Comparator<SocialContent> contentMostRecentComparator = new Comparator<>() {
        @Override
        public int compare(SocialContent first, SocialContent second) {
            return second.getPublishedOn().compareTo(first.getPublishedOn());
        }
    };

    @Override
    public String getMostPopularUser() {
        if (accountsRegistry.isEmpty()) {
            throw new NoUsersException("There are currently no users in the platform");
        }
        
        List<Account> result = new ArrayList<>(accountsRegistry.values());
        Collections.sort(result);
        return result.get(0).getUsername();
    }

    @Override
    public Collection<Content> findContentByTag(String tag) {
        if (tag == null) {
            throw new IllegalArgumentException("Provided tag is null.");
        }

        List<Content> contentWithTag = new ArrayList<>();
        for (SocialContent currentContent : contentRegistry.values()) {
            if (!currentContent.isExpired()) {
                Set<String> tags = new HashSet<>(currentContent.getTags());
                if (tags.contains(tag)) {
                    contentWithTag.add(currentContent);
                }
            }
        }
        return Collections.unmodifiableCollection(contentWithTag);
    }

    @Override
    public List<String> getActivityLog(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Provided username is null.");
        }
        Account user = accountsRegistry.get(username);
        if (user == null) {
            throw new UsernameNotFoundException("User with username " + username + " does not exist in the platform.");
        }

        List<String> logs = user.getActivityLog();
        Collections.reverse(logs);
        return logs;
    }
}

