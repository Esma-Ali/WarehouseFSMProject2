import java.io.Serializable;

public class WaitlistItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Client client;
    private int quantity;

    public WaitlistItem(Client client, int quantity) {
        if (client == null || quantity <= 0) {
            throw new IllegalArgumentException(
                    "A client and positive quantity are required.");
        }

        this.client = client;
        this.quantity = quantity;
    }

    public Client getClient() {
        return client;
    }

    public int getQuantity() {
        return quantity;
    }

    // Used when a shipment only partly fills this request
    public void deductQuantity(int filled) {
        if (filled <= 0 || filled > quantity) {
            throw new IllegalArgumentException(
                    "Filled quantity must be between 1 and "
                    + quantity + ".");
        }

        quantity -= filled;
    }

    @Override
    public String toString() {
        return "Client ID: " + client.getId() + ", Name: " + client.getName() + ", Waiting quantity: " + quantity;
    }
}
