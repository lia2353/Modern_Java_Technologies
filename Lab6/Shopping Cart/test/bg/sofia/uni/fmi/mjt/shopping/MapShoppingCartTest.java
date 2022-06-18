package bg.sofia.uni.fmi.mjt.shopping;

import bg.sofia.uni.fmi.mjt.shopping.item.Apple;
import bg.sofia.uni.fmi.mjt.shopping.item.Chocolate;
import bg.sofia.uni.fmi.mjt.shopping.item.Item;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MapShoppingCartTest {
    private static final String ID_RED_APPLE = "red-apples";
    private static final Item ITEM_RED_APPLE = new Apple(ID_RED_APPLE);
    private static final Item ITEM_RED_APPLE_SAME = new Apple(ID_RED_APPLE);
    private static final String ID_GREEN_APPLE = "green-apples";
    private static final Item ITEM_GREEN_APPLE = new Apple(ID_GREEN_APPLE);
    private static final String ID_BLACK_CHOCOLATE = "black-chocolate";
    private static final Item ITEM_BLACK_CHOCOLATE = new Chocolate(ID_BLACK_CHOCOLATE);
    private static final String ID_WHITE_CHOCOLATE = "white-chocolate";
    private static final Item ITEM_WHITE_CHOCOLATE = new Chocolate(ID_WHITE_CHOCOLATE);

    private ShoppingCart cart;
    private List<Item> expectedItems;
    private List<Item> actualItems;

    @Mock
    private ProductCatalog catalog = Mockito.mock(ProductCatalog.class);

    @Before
    public void setUp() {
        cart = new MapShoppingCart(catalog);
        expectedItems = new ArrayList<>();
        actualItems = new ArrayList<>();
    }

    //-----getUniqueItems()-----//
    @Test
    public void testGetUniqueItemsWithNoItems() {
        int expectedSize = 0;
        assertEquals(expectedSize, cart.getUniqueItems().size());
    }

    @Test
    public void testGetUniqueItemsWithOneItem() {
        cart.addItem(ITEM_RED_APPLE);

        expectedItems.add(ITEM_RED_APPLE);
        int expectedSize = 1;

        actualItems.addAll(cart.getUniqueItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    @Test
    public void testGetUniqueItemsWithUniqueItems() {
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_GREEN_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);

        expectedItems = List.of(ITEM_RED_APPLE, ITEM_GREEN_APPLE, ITEM_BLACK_CHOCOLATE);
        int expectedSize = 3;

        actualItems.addAll(cart.getUniqueItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    @Test
    public void testGetUniqueItemsWithDuplicateItems() {
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_GREEN_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE_SAME);

        expectedItems = List.of(ITEM_RED_APPLE, ITEM_GREEN_APPLE, ITEM_BLACK_CHOCOLATE);
        int expectedSize = 3;

        actualItems.addAll(cart.getUniqueItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    //-----getSortedItems()-----//
    @Test
    public void testGetSortedItemsWithNoItems() {
        int expectedSize = 0;
        assertEquals(expectedSize, cart.getSortedItems().size());
    }

    @Test
    public void testGetSortedItemsWithOneItem() {
        cart.addItem(ITEM_RED_APPLE);

        expectedItems.add(ITEM_RED_APPLE);
        int expectedSize = 1;

        actualItems.addAll(cart.getSortedItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    @Test
    public void testGetSortedItemsWithManyDuplicateItems() {
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_GREEN_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE_SAME);

        expectedItems= List.of(ITEM_RED_APPLE, //3 times
                ITEM_BLACK_CHOCOLATE, //2 times
                ITEM_GREEN_APPLE); //1 time
        int expectedSize = 3;

        actualItems.addAll(cart.getSortedItems());

        assertEquals(expectedSize, actualItems.size());
        assertEquals(ITEM_RED_APPLE, actualItems.get(0));
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    @Test
    public void testGetSortedItemsWithManyUniqueItems() {
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_GREEN_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_WHITE_CHOCOLATE);

        expectedItems = List.of(ITEM_RED_APPLE, ITEM_GREEN_APPLE, ITEM_BLACK_CHOCOLATE, ITEM_WHITE_CHOCOLATE);
        int expectedSize = 4;

        actualItems.addAll(cart.getSortedItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    //-----addItem()-----//
    @Test(expected = IllegalArgumentException.class)
    public void testAddItemWithNullItem() {
        cart.addItem(null);
    }

    @Test
    public void testAddItemWithAppleItem() {
        cart.addItem(ITEM_RED_APPLE);

        assertTrue(cart.getUniqueItems().contains(ITEM_RED_APPLE));
    }

    @Test
    public void testAddItemWithChocolateItem() {
        cart.addItem(ITEM_BLACK_CHOCOLATE);

        assertTrue(cart.getUniqueItems().contains(ITEM_BLACK_CHOCOLATE));
    }

    //-----removeItem()-----//
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveItemWithNullItem() {
        cart.removeItem(null);
    }

    @Test(expected = ItemNotFoundException.class)
    public void testRemoveItemWithNonExistingItem() {
        cart.removeItem(ITEM_RED_APPLE);
    }

    @Test
    public void testRemoveItemWithOneOccurrence() {
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE);
        int cartSize = 2;
        assertEquals(cartSize, cart.getUniqueItems().size());

        expectedItems.add(ITEM_BLACK_CHOCOLATE);
        int expectedSize = 1;

        cart.removeItem(ITEM_RED_APPLE);
        actualItems.addAll(cart.getUniqueItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    @Test
    public void testRemoveItemWithMultipleOccurrences() {
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        int cartSize = 2;
        assertEquals(cartSize, cart.getUniqueItems().size());

        expectedItems = List.of(ITEM_RED_APPLE, ITEM_BLACK_CHOCOLATE);
        int expectedSize = 2;

        cart.removeItem(ITEM_BLACK_CHOCOLATE);
        actualItems.addAll(cart.getUniqueItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    @Test
    public void testRemoveItemRemoveAllOccurrences() {
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);
        int cartSize = 2;
        assertEquals(cartSize, cart.getUniqueItems().size());

        expectedItems.add(ITEM_RED_APPLE);
        int expectedSize = 1;

        cart.removeItem(ITEM_BLACK_CHOCOLATE);
        cart.removeItem(ITEM_BLACK_CHOCOLATE);
        cart.removeItem(ITEM_BLACK_CHOCOLATE);
        actualItems.addAll(cart.getUniqueItems());

        assertEquals(expectedSize, actualItems.size());
        assertTrue(actualItems.containsAll(expectedItems));
        assertTrue(expectedItems.containsAll(actualItems));
    }

    //-----getTotal()-----//
    @Test
    public void testGetTotalWithNoItems() {
        Mockito.when(catalog.getProductInfo(ID_RED_APPLE)).thenReturn(new ProductInfo("Red apples",
                "From Bulgaria", 1.60));
        Mockito.when(catalog.getProductInfo(ID_GREEN_APPLE)).thenReturn(new ProductInfo("Green apples",
                "From Turkey", 1.80));
        Mockito.when(catalog.getProductInfo(ID_BLACK_CHOCOLATE)).thenReturn(new ProductInfo("Black chocolate",
                "99% cacao", 3.50));

        double expectedTotal = 0.0;
        assertEquals(expectedTotal, cart.getTotal(), 0);

        Mockito.verify(catalog, Mockito.times(0)).getProductInfo(ID_RED_APPLE);
        Mockito.verify(catalog, Mockito.times(0)).getProductInfo(ID_GREEN_APPLE);
        Mockito.verify(catalog, Mockito.times(0)).getProductInfo(ID_BLACK_CHOCOLATE);
    }

    @Test
    public void testGetTotalWithManyItems() {
        Mockito.when(catalog.getProductInfo(ID_RED_APPLE)).thenReturn(new ProductInfo("Red apples",
                "From Bulgaria", 1.60));
        Mockito.when(catalog.getProductInfo(ID_GREEN_APPLE)).thenReturn(new ProductInfo("Green apples",
                "From Turkey", 1.80));
        Mockito.when(catalog.getProductInfo(ID_BLACK_CHOCOLATE)).thenReturn(new ProductInfo("Black chocolate",
                "99% cacao", 3.50));

        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_RED_APPLE);
        cart.addItem(ITEM_GREEN_APPLE);
        cart.addItem(ITEM_BLACK_CHOCOLATE);

        double expectedTotal = 8.5;
        assertEquals(expectedTotal, cart.getTotal(), 0);

        Mockito.verify(catalog, Mockito.times(1)).getProductInfo(ID_RED_APPLE);
        Mockito.verify(catalog, Mockito.times(1)).getProductInfo(ID_GREEN_APPLE);
        Mockito.verify(catalog, Mockito.times(1)).getProductInfo(ID_BLACK_CHOCOLATE);
    }
}
