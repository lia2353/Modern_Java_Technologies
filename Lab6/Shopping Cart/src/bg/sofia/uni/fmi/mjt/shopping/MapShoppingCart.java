package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class MapShoppingCart extends AbstractShoppingCart {
    public Map<Item, Integer> items;

    public MapShoppingCart(ProductCatalog catalog) {
        super(catalog);
        this.items = new HashMap<>();
    }

    @Override
    public Collection<Item> getUniqueItems() {
        return items.keySet();
    }

    @Override
    public void addItem(Item item) {
        validateItemNotNull(item);

        Integer occurrences = items.get(item);
        items.put(item, occurrences == null ? 1 : ++occurrences);
    }

    @Override
    public void removeItem(Item item) {
        validateItemNotNull(item);
        if (!items.containsKey(item)) {
            throw new ItemNotFoundException(String.format(ITEM_NOT_FOUND_MESSAGE, item.getId()));
        }

        Integer occurrences = items.get(item);
        if (--occurrences == 0) {
            items.remove(item);
        } else {
            items.replace(item, occurrences);
        }
    }

    @Override
    public double getTotal() {
        double total = 0;
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            ProductInfo info = catalog.getProductInfo(entry.getKey().getId());
            total += (info.price() * entry.getValue());
        }
        return total;
    }

    @Override
    public Collection<Item> getSortedItems() {
        SortedSet<Item> sortedItems = new TreeSet<>(new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                int result = items.get(item2).compareTo(items.get(item1));
                //if result == 0, items are different, but the quantities are equal, hence the ordering does not matter
                return (result == 0) ? 1 : result;
            }
        });
        sortedItems.addAll(items.keySet());
        return sortedItems;
    }

}
