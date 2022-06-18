package bg.sofia.uni.fmi.mjt.wish.list;

public class Main {
    public static void main(String[] args) {
        WishListServer server = new WishListServer(7777);
        server.start();
    }
}
