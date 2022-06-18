package bg.sofia.uni.fmi.mjt.wish.list.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WishlistsData {
    private final Map<String, List<String>> usersWishLists;

    public WishlistsData() {
        usersWishLists = new HashMap<>();
    }

    public boolean isEmpty() {
        return usersWishLists.isEmpty();
    }

    public boolean isEmptyUsersWishList(String username) {
        if (!usersWishLists.isEmpty()) {
            return usersWishLists.get(username) == null;
        }
        return true;
    }

    public void addWish(String username, String wish) {
        if (isEmptyUsersWishList(username)) {
            usersWishLists.put(username, new ArrayList<>(List.of(wish)));
        } else {
            usersWishLists.get(username).add(wish);
        }
    }

    public boolean alreadyContainsWish(String username, String wish) {
        if (!isEmptyUsersWishList(username)) {
            return usersWishLists.get(username).contains(wish);
        }
        return false;
    }

    public boolean containsOnlyUsersWishlist(String username) {
        return usersWishLists.size() == 1 && usersWishLists.containsKey(username);
    }

    public String getRandomUsernameWithWishList(String notWithThisUsername) {
        String username;
        do {
            // Random number in [min; max] -> (Math.random() * (max - min)) + min);
            int index = (int) (Math.random() * usersWishLists.size());
            username = new ArrayList<>(usersWishLists.keySet()).get(index);
        } while (username.equals(notWithThisUsername));

        return username;
    }

    public String getWishlist(String username) {
        String wishes = String.join(", ", usersWishLists.get(username));
        return wishes;
    }

    public void removeWishlist(String username) {
        usersWishLists.remove(username);
    }

}
