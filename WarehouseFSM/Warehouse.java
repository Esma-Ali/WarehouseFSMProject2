import java.io.Serializable;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class Warehouse implements Serializable {
    private static final long serialVersionUID = 1L;

    private static Warehouse instance;
    private ClientList clientList;
    private ProductList productList;

    private Warehouse() {
        clientList = new ClientList();
        productList = new ProductList();
    }

    public static Warehouse instance() {
        if (instance == null) {
            instance = new Warehouse();
        }

        return instance;
    }

    public Client addClient(String name, String address) {
        Client client = new Client(name, address);

        if (clientList.insertClient(client)) {
            return client;
        }

        return null;
    }

    public Iterator<Client> getClients() {
        return clientList.getClients();
    }

    public Product addProduct(String name, int quantity, double price) {
        Product product = new Product(name, quantity, price);

        if (productList.insertProduct(product)) {
            return product;
        }

        return null;
    }

    public Iterator<Product> getProducts() {
        return productList.getProducts();
    }

    public Client getClient(String clientID) {
        return clientList.findClient(clientID);
    }

    public Product getProduct(String productID) {
        return productList.findProduct(productID);
    }

    public boolean addToWishlist(
            String clientID, String productID, int quantity) {

        Client client = getClient(clientID);
        Product product = getProduct(productID);

        if (client == null || product == null || quantity <= 0) {
            return false;
        }

        return client.addToWishlist(product, quantity);
    }

    public Iterator<WishlistItem> getClientWishlist(String clientID) {
        Client client = getClient(clientID);

        if (client == null) {
            return null;
        }

        return client.getWishlist();
    }
        public List<WishlistItem> getClientWishlistSnapshot(String clientID) {
        Iterator<WishlistItem> iterator = getClientWishlist(clientID);

        if (iterator == null) {
            return null;
        }

        List<WishlistItem> snapshot = new ArrayList<>();

        while (iterator.hasNext()) {
            WishlistItem item = iterator.next();
            snapshot.add(new WishlistItem(
                    item.getProduct(),
                    item.getQuantity()));
        }

        return snapshot;
    }
        public boolean processOrder(String clientID) {
        Client client = getClient(clientID);
        List<WishlistItem> items =
                getClientWishlistSnapshot(clientID);

        if (client == null || items == null || items.isEmpty()) {
            return false;
        }

        Invoice invoice = new Invoice(clientID);
        boolean hasSuppliedItems = false;

        for (WishlistItem item : items) {
            Product product = item.getProduct();
            int requested = item.getQuantity();

            int supplied = product.supply(requested);
            int shortage = requested - supplied;

            if (supplied > 0) {
                invoice.addItem(
                        product, supplied, product.getPrice());

                hasSuppliedItems = true;
            }

            if (shortage > 0) {
                product.addToWaitlist(client, shortage);
            }

            client.removeFromWishlist(product.getId());
        }

        if (hasSuppliedItems) {
            client.debit(invoice.getTotal());
            client.addInvoice(invoice);
        }

        return true;
    }
    // Fills the product's waitlist in order before adding anything to stock
    // Returns the invoices created, or null if the product ID or quantity is invalid
    public Iterator<Invoice> receiveShipment(
            String productID, int received) {

        Product product = getProduct(productID);

        if (product == null || received <= 0) {
            return null;
        }

        List<Invoice> filledInvoices = new ArrayList<>();
        int remaining = received;
        WaitlistItem item;

        while (remaining > 0
                && (item = product.getFirstWaitlistItem()) != null) {
            Client client = item.getClient();
            int waiting = item.getQuantity();
            int filled = Math.min(remaining, waiting);

            Invoice invoice = new Invoice(client.getId());
            invoice.addItem(product, filled, product.getPrice());

            client.debit(invoice.getTotal());
            client.addInvoice(invoice);

            if (filled == waiting) {
                product.removeFirstWaitlistItem();
            } else {
                item.deductQuantity(filled);
            }

            remaining -= filled;
            filledInvoices.add(invoice);
        }

        if (remaining > 0) {
            product.addStock(remaining);
        }

        return filledInvoices.iterator();
    }

    public Iterator<WaitlistItem> getProductWaitlist(
            String productID) {

        Product product = getProduct(productID);

        if (product == null) {
            return null;
        }

        return product.getWaitlist();
    }

    public Double recordPayment(String clientID, double amount) {
        Client client = getClient(clientID);

        if (client == null || !Double.isFinite(amount) || amount <= 0) {
            return null;
        }

        client.credit(amount);
        return client.getBalance();
    }
    public Iterator<Invoice> getClientInvoices(String clientID) {
        Client client = getClient(clientID);

        if (client == null) {
            return null;
        }

        return client.getInvoices();
    }
}
