
import java.util.Iterator;

public class ManagerMenuState extends WarehouseState {

    private static ManagerMenuState instance;
    private final Warehouse warehouse;

    private static final int LOGOUT = 0;
    private static final int ADD_PRODUCT = 1;
    private static final int SHOW_WAITLIST = 2;
    private static final int RECEIVE_SHIPMENT = 3;
    private static final int BECOME_CLERK = 4;
    private static final int HELP = 5;

    private ManagerMenuState() {
        warehouse = Warehouse.instance();
    }

    public static ManagerMenuState instance() {
        if (instance == null) {
            instance = new ManagerMenuState();
        }
        return instance;
    }

    public void run() {
        process();
    }

    public void help() {
        System.out.println("\n--- Manager Menu ---");
        System.out.println(LOGOUT + " to log out");
        System.out.println(ADD_PRODUCT + " to add a product");
        System.out.println(SHOW_WAITLIST + " to show a product waitlist");
        System.out.println(RECEIVE_SHIPMENT + " to receive a shipment");
        System.out.println(BECOME_CLERK + " to switch to Clerk Menu");
        System.out.println(HELP + " for help");
    }

    public int getCommand() {
        while (true) {
            int command = WarehouseInput.getNumber("Enter command:");

            if (command >= LOGOUT && command <= HELP) {
                return command;
            }

            System.out.println("Please enter a number between 0 and 5.");
        }
    }

    public void addProduct() {
        String name = WarehouseInput.getToken("Enter product name");
        int quantity = WarehouseInput.getNumber("Enter quantity");
        double price = WarehouseInput.getAmount("Enter unit price");

        if (quantity < 0 || price < 0) {
            System.out.println("Quantity and price cannot be negative.");
            return;
        }

        try {
            Product result = warehouse.addProduct(name, quantity, price);

            if (result != null) {
                System.out.println("Product added: " + result);
            } else {
                System.out.println("Product could not be added.");
            }
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public void showWaitlist() {
        String productID = WarehouseInput.getToken("Enter product ID");

        Iterator<WaitlistItem> items =
                warehouse.getProductWaitlist(productID);

        if (items == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("--- Product Waitlist ---");

        if (!items.hasNext()) {
            System.out.println("No clients are waiting for this product.");
            return;
        }

        while (items.hasNext()) {
            System.out.println(items.next());
        }
    }

    public void receiveShipment() {
        String productID = WarehouseInput.getToken("Enter product ID");
        int quantity = WarehouseInput.getNumber("Enter received quantity");

        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return;
        }

        Iterator<Invoice> invoices =
                warehouse.receiveShipment(productID, quantity);

        if (invoices == null) {
            System.out.println("Shipment could not be processed. Check the product ID.");
            return;
        }

        System.out.println("--- Shipment Results ---");

        if (!invoices.hasNext()) {
            System.out.println("No waitlist orders were filled. Remaining stock was added.");
            return;
        }

        while (invoices.hasNext()) {
            System.out.println(invoices.next());
        }
    }

    public void becomeClerk() {
        if (WarehouseContext.instance().becomeClerk()) {
            System.out.println("Switching to Clerk Menu.");
        } else {
            System.out.println("Could not switch to Clerk Menu.");
        }
    }

    public void logout() {
        WarehouseContext.instance().logout();
    }

    public void process() {
        help();

        while (true) {
            int command = getCommand();

            switch (command) {
                case ADD_PRODUCT:
                    addProduct();
                    break;

                case SHOW_WAITLIST:
                    showWaitlist();
                    break;

                case RECEIVE_SHIPMENT:
                    receiveShipment();
                    break;

                case BECOME_CLERK:
                    becomeClerk();
                    return;

                case LOGOUT:
                    logout();
                    return;

                case HELP:
                    help();
                    break;
            }
        }
    }
}
