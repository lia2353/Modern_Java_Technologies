package bg.sofia.uni.fmi.mjt.revolut;

import bg.sofia.uni.fmi.mjt.revolut.account.Account;
import bg.sofia.uni.fmi.mjt.revolut.account.BGNAccount;
import bg.sofia.uni.fmi.mjt.revolut.account.EURAccount;
import bg.sofia.uni.fmi.mjt.revolut.card.Card;
import bg.sofia.uni.fmi.mjt.revolut.card.PhysicalCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualOneTimeCard;
import bg.sofia.uni.fmi.mjt.revolut.card.VirtualPermanentCard;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Card[] cards = new Card[4];
        cards[0] = new PhysicalCard("c-1", 1234, LocalDate.of(2022, 12, 3));
        cards[1] = new VirtualOneTimeCard("c-2", 4321, LocalDate.of(2023, 1, 5));
        cards[2] = new VirtualPermanentCard("c-3", 3434, LocalDate.of(2021, 2, 3));
        cards[3] = new VirtualPermanentCard("c-4", 3434, LocalDate.of(2020, 2, 3));

        Account[] accounts = new Account[4];
        accounts[0] = new BGNAccount("BG-1", 100.45);
        accounts[1] = new EURAccount("EU-1", 1000);
        accounts[2] = new BGNAccount("BG-2", 10000.40);
        accounts[3] = new EURAccount("EU-2", 30.60);

        Revolut myRevolut = new Revolut(accounts, cards);

        //System.out.println(myRevolut.getTotalAmount());

        System.out.println(myRevolut.payOnline(cards[0], 1234, 500, "EUR", "amazon.com"));
        //System.out.println(myRevolut.payOnline(cards[1], 4321, 5, "BGN", "amazon.biz"));
        //System.out.println(myRevolut.pay(cards[2], 3434, 5, "BGN"));
        //System.out.println(myRevolut.pay(cards[0], 3434, 10, "EUR"));
        System.out.println(myRevolut.pay(cards[3], 3434, 10, "EUR"));
        //System.out.println(myRevolut.pay(cards[2], 4321, 10, "EUR"));


        System.out.println(accounts[0].getAmount() + " " + accounts[1].getAmount() + " " +
                accounts[2].getAmount() + " " + accounts[3].getAmount());
    }
}
