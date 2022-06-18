package bg.sofia.uni.fmi.mjt.shopping.item;

import java.util.Objects;

public class BaseItem implements Item{
    private String id;

    public BaseItem(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseItem)) return false;
        BaseItem baseItem = (BaseItem) o;
        return id.equals(baseItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
