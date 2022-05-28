package bg.sofia.uni.fmi.mjt.netflix;

import bg.sofia.uni.fmi.mjt.netflix.account.Account;
import bg.sofia.uni.fmi.mjt.netflix.content.*;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.Genre;
import bg.sofia.uni.fmi.mjt.netflix.content.enums.PgRating;
import bg.sofia.uni.fmi.mjt.netflix.exceptions.ContentUnavailableException;
import bg.sofia.uni.fmi.mjt.netflix.platform.Netflix;
import bg.sofia.uni.fmi.mjt.netflix.platform.StreamingService;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {

        Account a1 = new Account("Krisi", LocalDateTime.of(1996, 7, 9, 15, 30));
        Account a2 = new Account("Petq", LocalDateTime.of(2010, 7, 9, 15, 30));
        Streamable m1 = new Movie("The Notebook", Genre.ACTION, PgRating.PG13, 123);
        Streamable m2 = new Movie("Christopher Robin", Genre.COMEDY, PgRating.G, 104);
        Episode e1 = new Episode("The One Where Monica Gets a Roommate", 22);
        Episode e2 = new Episode("The One with the Monkey", 22);
        Streamable s1 = new Series("Friends", Genre.COMEDY, PgRating.NC17, new Episode[]{e1, e2});

        Account[] accounts = {a1, a2};
        Streamable[] streamableContent = {m1, s1, m2};


        StreamingService myNetflix = new Netflix(accounts, streamableContent);
        try {
            myNetflix.watch(a1, "The Notebook");
            myNetflix.watch(a1, "Friends");
            myNetflix.watch(a1, "Friends");
            //myNetflix.watch(a2, "Friends"); //age restricted
            //myNetflix.watch(a2, "Avatar"); //content not found
            //myNetflix.watch(new Account("user", LocalDateTime.of(1977, 8, 14, 9, 30)), "The Notebook"); //user not found
        } catch (ContentUnavailableException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("The most watched content is " + myNetflix.mostViewed().getTitle());

        System.out.println("Total watched time: " + myNetflix.totalWatchedTimeByUsers() / 60 + " hours and " + myNetflix.totalWatchedTimeByUsers() % 60 + " minutes.");
    }
}
