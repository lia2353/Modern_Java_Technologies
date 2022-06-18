package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class ListShoppingCart extends AbstractShoppingCart {
    List<Item> items;

    public ListShoppingCart(ProductCatalog catalog) {
        super(catalog);
        this.items = new ArrayList<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return new HashSet<>(items);
    }

    @Override
    public void addItem(Item item) {
        validateItemNotNull(item);
        items.add(item);
    }

    @Override
    public void removeItem(Item item) {
        validateItemNotNull(item);
        if (!items.contains(item)) {
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, item.getId()));
        }

        items.remove(item);
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Item item : items) {
            ProductInfo info = catalog.getProductInfo(item.getId());
            total += info.price();
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        Map<Item, Integer> itemToQuantity = createMapWithQuantity();
        SortedSet<Item> sortedItems = new TreeSet<>(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                int result = itemToQuantity.get(item2).compareTo(itemToQuantity.get(item1));
                //if result == 0, items are different, but the quantities are equal, hence the ordering does not matter
                return (result == 0) ? 1 : result;
            }
        });

        sortedItems.addAll(itemToQuantity.keySet());
        return sortedItems;
    }

    private Map<Item, Integer> createMapWithQuantity() {
        Map<Item, Integer> itemToQuantity = new HashMap<>();
        for (Item item : items) {
            int quantity = itemToQuantity.containsKey(item) ? itemToQuantity.get(item) + 1 : 1;
            itemToQuantity.put(item, quantity);
        }
        return itemToQuantity;
    }
}