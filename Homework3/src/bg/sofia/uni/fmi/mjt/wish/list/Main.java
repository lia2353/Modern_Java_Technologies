package bg.sofia.uni.fmi.mjt.wish.list;

import bg.sofia.uni.fmi.mjt.wish.list.server.WishListServer;

public class Main {
    public static void main(String[] args) {
        WishListServer server = new WishListServer(7777);
        server.start();
        server.stop();
    }
}
