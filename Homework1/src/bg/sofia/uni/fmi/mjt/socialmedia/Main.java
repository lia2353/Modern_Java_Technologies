package bg.sofia.uni.fmi.mjt.socialmedia;

import bg.sofia.uni.fmi.mjt.socialmedia.account.Account;
import bg.sofia.uni.fmi.mjt.socialmedia.content.Content;
import bg.sofia.uni.fmi.mjt.socialmedia.content.SocialMediaContent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        EvilSocialInator media = new EvilSocialInator();
        //media.register(null);
        //media.register("bananator");
        media.register("bananator");
        media.register("iol");
        media.register("poly");
        media.register("iv");

        System.out.println(media.publishPost("bananator", LocalDateTime.now().minusDays(40), "Hi my first post"));
        System.out.println(media.publishPost("bananator", LocalDateTime.now().minusDays(2), "#Hi my second #post #like"));
        System.out.println(media.publishPost("poly", LocalDateTime.now().minusDays(10), "My best friends are @iv @iv @lora #forevertogether"));
        System.out.println(media.publishStory("bananator", LocalDateTime.now(), "Drinking #coffee with @iv and    @poly #post"));

        media.like("poly", "bananator-3");
        media.like("bananator", "bananator-3");
        media.comment("poly", "...and chilling", "bananator-3");

        List<Content> contentWithTag = new ArrayList<>(media.findContentByTag("#no"));
        System.out.println(contentWithTag.size());

        contentWithTag = new ArrayList<>(media.findContentByTag("#post"));
        System.out.println(contentWithTag.size());

        List<String> logs = new ArrayList<>(media.getActivityLog("bananator"));
        System.out.println(logs);
        logs = new ArrayList<>(media.getActivityLog("poly"));
        System.out.println(logs);
        logs = new ArrayList<>(media.getActivityLog("iol"));
        System.out.println(logs);
        logs = new ArrayList<>(media.getActivityLog("iv"));
        System.out.println(logs);

        System.out.println(media.getMostPopularUser());

        System.out.println("here");
        for (Content c : media.getNMostPopularContent(6)) {
            System.out.println(c.getId());
        }

        System.out.println(media.publishPost("poly", LocalDateTime.now().minusDays(1), "Will you like my post"));
        media.like("poly", "poly-4");
        media.like("bananator", "poly-4");
        media.comment("iv", "why?", "poly-4");
        media.comment("poly", "To become popular", "poly-4");

        System.out.println("here");
        for (Content c : media.getNMostPopularContent(2)) {
            System.out.println(c.getId());
        }

        System.out.println("here");
        for (Content c : media.getNMostRecentContent("bananator", 3)) {
            System.out.println(c.getId());
        }

        System.out.println("here");
        for (Content c : media.getNMostRecentContent("poly", 3)) {
            System.out.println(c.getId());
        }

    }
}
