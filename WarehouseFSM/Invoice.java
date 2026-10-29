import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Invoice implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int idCounter = 1;

    private final String id;
    private final String clientID;
    private final List<InvoiceItem> items = new LinkedList<>();

    public Invoice(String clientID) {
        this.id = "I" + idCounter++;
        this.clientID = clientID;
    }

    // The price is copied so the invoice price is correct even if the product's price changes later
    public void addItem(Product product, int quantity, double unitPrice) {
        if (product == null || quantity <= 0 || unitPrice < 0) {
            throw new IllegalArgumentException("A product, positive quantity, and valid price are required.");
        }

        items.add(new InvoiceItem(product.getId(), product.getName(), quantity, unitPrice));
    }

    public String getId() {
        return id;
    }

    public String getClientID() {
        return clientID;
    }

    public double getTotal() {
        double total = 0;

        for (InvoiceItem item : items) {
            total += item.getTotal();
        }

        return total;
    }

    @Override
    public String toString() {
        String result = "Invoice " + id + " for Client " + clientID;

        for (InvoiceItem item : items) {
            result += "\n  " + item;
        }

        return result + "\n  Invoice total: $"
                + String.format("%.2f", getTotal());
    }

    private static class InvoiceItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String productID;
        private final String productName;
        private final int quantity;
        private final double unitPrice;

        InvoiceItem(String productID, String productName, int quantity, double unitPrice) {
            this.productID = productID;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        double getTotal() {
            return quantity * unitPrice;
        }

        @Override
        public String toString() {
            return "Product ID: " + productID + ", Name: " + productName + ", Quantity: " + quantity + ", Unit price: $" + String.format("%.2f", unitPrice) + ", Total: $" + String.format("%.2f", getTotal());
        }
    }
}
