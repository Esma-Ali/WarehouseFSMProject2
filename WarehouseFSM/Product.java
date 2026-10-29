import java.io.Serializable;
import java.util.Iterator;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;

    private String id;
    private String name;
    private int quantity;
    private double price;
    private final Waitlist waitlist = new Waitlist();

    public Product(String name, int quantity, double price) {
        if (name == null || name.trim().isEmpty()
                || quantity < 0
                || !Double.isFinite(price) || price < 0) {
            throw new IllegalArgumentException(
                    "Valid name, quantity, and price are required.");
        }

        this.id = "P" + idCounter++;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public int supply(int requested) {
        if (requested <= 0) {
            throw new IllegalArgumentException(
                    "Requested quantity must be positive.");
        }

        int supplied = Math.min(requested, quantity);
        quantity -= supplied;
        return supplied;
    }

    public void addStock(int received) {
        if (received <= 0) {
            throw new IllegalArgumentException(
                    "Received quantity must be positive.");
        }

        quantity = Math.addExact(quantity, received);
    }

    public void addToWaitlist(Client client, int quantity) {
        waitlist.add(client, quantity);
    }

    public WaitlistItem getFirstWaitlistItem() {
        return waitlist.getFirst();
    }

    public WaitlistItem removeFirstWaitlistItem() {
        return waitlist.removeFirst();
    }

    public Iterator<WaitlistItem> getWaitlist() {
        return waitlist.getIterator();
    }

    @Override
    public String toString() {
        return "ID: " + id
                + ", Name: " + name
                + ", Quantity: " + quantity
                + ", Price: $" + String.format("%.2f", price);
    }
}
