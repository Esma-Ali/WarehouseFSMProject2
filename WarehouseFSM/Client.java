import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;

    private String id;
    private String name;
    private String address;
    private double balance;
    private Wishlist wishlist;
    private List<Invoice> invoices;

    public Client(String name, String address) {
        this.id = "C" + idCounter++;
        this.name = name;
        this.address = address;
        this.balance = 0.0;
        this.wishlist = new Wishlist();
        this.invoices = new LinkedList<Invoice>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void debit(double amount) {
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Debit amount must be a non-negative number.");
        }

        balance += amount;
    }

    public void credit(double amount) {
        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be a positive number.");
        }

        balance -= amount;
    }

    public void addInvoice(Invoice invoice) {
        if (invoice != null) {
            invoices.add(invoice);
        }
    }

    public Iterator<Invoice> getInvoices() {
        return invoices.iterator();
    }

    public boolean removeFromWishlist(String productID) {
        return wishlist.removeProduct(productID);
    }

    public boolean addToWishlist(Product product, int quantity) {
        return wishlist.addProduct(product, quantity);
    }

    public Iterator<WishlistItem> getWishlist() {
        return wishlist.getIterator();
    }

    @Override
    public String toString() {
        return "Client ID: " + id
                + ", Name: " + name
                + ", Address: " + address
                + ", Balance: $" + String.format("%.2f", balance);
    }
}
