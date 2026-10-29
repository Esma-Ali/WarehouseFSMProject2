import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Wishlist implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<WishlistItem> items;

    public Wishlist() {
        items = new LinkedList<WishlistItem>();
    }

    public boolean addProduct(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            return false;
        }

        // Update the quantity if this product is already in the wishlist.
        for (WishlistItem item : items) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(quantity);
                return true;
            }
        }

        return items.add(new WishlistItem(product, quantity));
    }

    public boolean removeProduct(String productID) {
        Iterator<WishlistItem> iterator = items.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getProduct().getId().equals(productID)) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }

    public Iterator<WishlistItem> getIterator() {
        return items.iterator();
    }
}
