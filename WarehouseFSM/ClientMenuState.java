import java.util.Iterator;

public class ClientMenuState extends WarehouseState {
    private static ClientMenuState instance;
    private static Warehouse warehouse;
    private WarehouseContext context;
    private String clientID;

    private static final int LOGOUT = 0;
    private static final int SHOW_DETAILS = 1;
    private static final int SHOW_PRODUCTS = 2;
    private static final int SHOW_TRANSACTIONS = 3;
    private static final int ADD_TO_WISHLIST = 4;
    private static final int DISPLAY_WISHLIST = 5;
    private static final int PLACE_ORDER = 6;
    private static final int HELP = 7;

    private ClientMenuState() {
        super();
        warehouse = Warehouse.instance();
        context = WarehouseContext.instance();
    }

    public static ClientMenuState instance() {
        if (instance == null) {
            instance = new ClientMenuState();
        }
        return instance;
    }

    // Input goes through WarehouseInput so every state shares one reader
    // on System.in.
    public String getToken(String prompt) {
        return WarehouseInput.getToken(prompt);
    }

    private boolean yesOrNo(String prompt) {
        return WarehouseInput.yesOrNo(prompt);
    }

    public int getNumber(String prompt) {
        return WarehouseInput.getNumber(prompt);
    }

    public int getCommand() {
        while (true) {
            int command = getNumber("Enter command (" + HELP + " for help)");

            if (command >= LOGOUT && command <= HELP) {
                return command;
            }

            System.out.println("Enter a number between "
                    + LOGOUT + " and " + HELP + ".");
        }
    }

    public void help() {
        System.out.println("\n---- Client Menu ----");
        System.out.println(LOGOUT + " to logout");
        System.out.println(SHOW_DETAILS + " to show my details");
        System.out.println(SHOW_PRODUCTS + " to show products");
        System.out.println(SHOW_TRANSACTIONS + " to show my transactions");
        System.out.println(ADD_TO_WISHLIST
                + " to add a product to my wishlist");
        System.out.println(DISPLAY_WISHLIST + " to display my wishlist");
        System.out.println(PLACE_ORDER + " to place an order");
        System.out.println(HELP + " for help");
    }

    public void showDetails() {
        Client client = warehouse.getClient(clientID);

        if (client == null) {
            System.out.println("Client not found.");
            return;
        }

        System.out.println(client);
    }

    // Product.toString() includes the price.
    public void showProducts() {
        Iterator<Product> products = warehouse.getProducts();

        if (!products.hasNext()) {
            System.out.println("No products in the warehouse.");
            return;
        }

        while (products.hasNext()) {
            System.out.println(products.next());
        }
    }

    public void showTransactions() {
        Iterator<Invoice> invoices = warehouse.getClientInvoices(clientID);

        if (invoices == null || !invoices.hasNext()) {
            System.out.println("No transactions for this client.");
            return;
        }

        while (invoices.hasNext()) {
            System.out.println(invoices.next());
        }
    }

    public void addToWishlist() {
        do {
            String productID = getToken("Enter product ID (e.g. P1)");

            if (warehouse.getProduct(productID) == null) {
                System.out.println("Product ID not found.");
            } else {
                int quantity = getNumber("Enter quantity");

                // Adding a product that is already on the wishlist
                // replaces its quantity.
                if (warehouse.addToWishlist(clientID, productID, quantity)) {
                    System.out.println("Wishlist updated.");
                } else {
                    System.out.println("Quantity must be a positive number.");
                }
            }
        } while (yesOrNo("Add another product?"));
    }

    public void displayWishlist() {
        Iterator<WishlistItem> items = warehouse.getClientWishlist(clientID);

        if (items == null || !items.hasNext()) {
            System.out.println("Your wishlist is empty.");
            return;
        }

        while (items.hasNext()) {
            System.out.println(items.next());
        }
    }

    public void placeOrder() {
        int invoicesBefore = countInvoices();

        if (!warehouse.processOrder(clientID)) {
            System.out.println(
                    "Could not place an order. Your wishlist is empty.");
            return;
        }

        System.out.println("Order processed.");

        // Items that could not be supplied go on the product waitlists,
        // so an order may produce no new invoice.
        if (countInvoices() > invoicesBefore) {
            Iterator<Invoice> invoices =
                    warehouse.getClientInvoices(clientID);
            Invoice latest = null;

            while (invoices.hasNext()) {
                latest = invoices.next();
            }

            System.out.println(latest);
        }

        System.out.println("Any quantities that were out of stock have "
                + "been added to the product waitlists.");
    }

    private int countInvoices() {
        Iterator<Invoice> invoices = warehouse.getClientInvoices(clientID);
        int count = 0;

        while (invoices != null && invoices.hasNext()) {
            invoices.next();
            count++;
        }

        return count;
    }

    // WarehouseContext picks the next state: Login if the user originally
    // logged in as a client, Clerk if a clerk or manager became this client.
    public void logout() {
        context.logout();
    }

    public void process() {
        help();

        while (context.getCurrentState()
                == WarehouseContext.CLIENT_STATE) {
            switch (getCommand()) {
                case LOGOUT:
                    logout();
                    break;

                case SHOW_DETAILS:
                    showDetails();
                    break;

                case SHOW_PRODUCTS:
                    showProducts();
                    break;

                case SHOW_TRANSACTIONS:
                    showTransactions();
                    break;

                case ADD_TO_WISHLIST:
                    addToWishlist();
                    break;

                case DISPLAY_WISHLIST:
                    displayWishlist();
                    break;

                case PLACE_ORDER:
                    placeOrder();
                    break;

                case HELP:
                    help();
                    break;

                default:
                    break;
            }
        }
    }

    // Called by WarehouseContext. The client is whoever logged in, or
    // whoever a clerk chose with "become client".
    @Override
    public void run() {
        clientID = context.getActiveClientID();
        process();
    }
}