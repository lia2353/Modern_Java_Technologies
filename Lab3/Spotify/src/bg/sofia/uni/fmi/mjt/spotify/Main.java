package bg.sofia.uni.fmi.mjt.spotify;

import bg.sofia.uni.fmi.mjt.spotify.account.Account;
import bg.sofia.uni.fmi.mjt.spotify.account.FreeAccount;
import bg.sofia.uni.fmi.mjt.spotify.account.PremiumAccount;
import bg.sofia.uni.fmi.mjt.spotify.exceptions.*;
import bg.sofia.uni.fmi.mjt.spotify.library.Library;
import bg.sofia.uni.fmi.mjt.spotify.library.UserLibrary;
import bg.sofia.uni.fmi.mjt.spotify.playable.Audio;
import bg.sofia.uni.fmi.mjt.spotify.playable.Playable;
import bg.sofia.uni.fmi.mjt.spotify.playable.Video;
import bg.sofia.uni.fmi.mjt.spotify.playlist.Playlist;
import bg.sofia.uni.fmi.mjt.spotify.playlist.UserPlaylist;

public class Main {
    public static void main(String[] args) {

        Account freeAccount1 = new FreeAccount("user@gmail.com", new UserLibrary());
        Account freeAccount2 = new FreeAccount("user@abv.bg", new UserLibrary());
        Account proAccount = new PremiumAccount("username@gmail.com", new UserLibrary());

        Playable audio1 = new Audio("YMCA", "Village People", 1978, 4.01);
        Playable audio2 = new Audio("English lesson 1", "Oxford", 2015, 45.16);

        Playable video1 = new Video("Billie Jean", "Michael Jackson", 1982, 4.56);
        Playable video2 = new Video("Hung Up", "Madonna", 2005, 5.33);

        Playlist myVideos = new UserPlaylist("Old videos");
        try {
            myVideos.add(video1);
            myVideos.add(video2);
        } catch (PlaylistCapacityExceededException e) {
            System.out.println(e);
        }

        Library playlist1 = new UserLibrary();
        try {
            for (int i = 0; i < 22; ++i) {
                playlist1.add(myVideos);
            }
        } catch (LibraryCapacityExceededException e) {
            System.out.println(e);
        }
        try {
            for (int i = 0; i < 22; ++i) {
                playlist1.remove("Old videos");
            }
        } catch (EmptyLibraryException | PlaylistNotFoundException e) {
            System.out.println(e);
        }

        Account[] accounts = {freeAccount1, proAccount};
        Playable[] content = {audio1, audio2, video1, video2};
        StreamingService platform = new Spotify(accounts, content);

        try {
            for (int i = 0; i < 12; ++i) {
                platform.play(freeAccount1, "YMCA");
            }

            platform.play(proAccount, "Hung Up");
            platform.play(freeAccount2, "Billie Jean");
        } catch (AccountNotFoundException | PlayableNotFoundException e) {
            System.out.println(e);
        }

        try {
            platform.like(freeAccount1, "English lesson 1");
            platform.like(freeAccount1, "English lesson 2");
        } catch (AccountNotFoundException | PlayableNotFoundException | StreamingServiceException e) {
            System.out.println(e);
        }

        try {
            for (int i = 0; i < 21; ++i) {
                platform.like(proAccount, "Billie Jean");
            }
        } catch (AccountNotFoundException | PlayableNotFoundException | StreamingServiceException e) {
            System.out.println(e);
        }

        System.out.println("Email: " + freeAccount1.getEmail() + " Type: " + freeAccount1.getType());
        System.out.println("Ads: " + freeAccount1.getAdsListenedTo() + " Total listened content: " + freeAccount1.getTotalListenedContent()
                + " Total listened time: " + freeAccount1.getTotalListenTime());

        System.out.println("Email: " + proAccount.getEmail() + " Type: " + proAccount.getType());
        System.out.println("Ads: " + proAccount.getAdsListenedTo() + " Total listened content: " + proAccount.getTotalListenedContent()
                + " Total listened time: " + proAccount.getTotalListenTime());

        try {
            System.out.println(platform.findByTitle("YMCA").getTotalPlays());
        } catch (PlayableNotFoundException e) {
            System.out.println(e);
        }


        System.out.println(platform.getMostPlayed().getTitle());
        System.out.println(platform.getTotalListenTime());
        System.out.println(platform.getTotalPlatformRevenue());

    }
}
