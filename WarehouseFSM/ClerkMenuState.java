import java.util.Iterator;

public class ClerkMenuState extends WarehouseState {
    private static ClerkMenuState instance;
    private static Warehouse warehouse;
    private WarehouseContext context;

    private static final int LOGOUT = 0;
    private static final int ADD_CLIENT = 1;
    private static final int SHOW_PRODUCTS = 2;
    private static final int SHOW_CLIENTS = 3;
    private static final int SHOW_CLIENTS_WITH_BALANCE = 4;
    private static final int RECORD_PAYMENT = 5;
    private static final int BECOME_CLIENT = 6;
    private static final int HELP = 7;

    private ClerkMenuState() {
        super();
        warehouse = Warehouse.instance();
        context = WarehouseContext.instance();
    }

    public static ClerkMenuState instance() {
        if (instance == null) {
            instance = new ClerkMenuState();
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

    public double getAmount(String prompt) {
        return WarehouseInput.getAmount(prompt);
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
        System.out.println("\n---- Clerk Menu ----");
        System.out.println(LOGOUT + " to logout");
        System.out.println(ADD_CLIENT + " to add a client");
        System.out.println(SHOW_PRODUCTS + " to show products");
        System.out.println(SHOW_CLIENTS + " to show clients");
        System.out.println(SHOW_CLIENTS_WITH_BALANCE
                + " to show clients with an outstanding balance");
        System.out.println(RECORD_PAYMENT + " to record a payment");
        System.out.println(BECOME_CLIENT + " to become a client");
        System.out.println(HELP + " for help");
    }

    public void addClient() {
        do {
            String name = getToken("Enter client name");
            String address = getToken("Enter client address");
            Client result = warehouse.addClient(name, address);

            if (result == null) {
                System.out.println("Could not add client.");
            } else {
                System.out.println("Added " + result);
            }
        } while (yesOrNo("Add another client?"));
    }

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

    public void showClients() {
        Iterator<Client> clients = warehouse.getClients();

        if (!clients.hasNext()) {
            System.out.println("No clients in the warehouse.");
            return;
        }

        while (clients.hasNext()) {
            System.out.println(clients.next());
        }
    }

    // A positive balance means the client owes money.
    public void showClientsWithBalance() {
        Iterator<Client> clients = warehouse.getClients();
        boolean found = false;

        while (clients.hasNext()) {
            Client client = clients.next();

            if (client.getBalance() > 0) {
                System.out.println(client);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No clients have an outstanding balance.");
        }
    }

    public void recordPayment() {
        String clientID = getToken("Enter client ID");

        if (warehouse.getClient(clientID) == null) {
            System.out.println("Client ID not found.");
            return;
        }

        double amount = getAmount("Enter payment amount");
        Double balance = warehouse.recordPayment(clientID, amount);

        if (balance == null) {
            System.out.println("Payment must be a positive amount.");
        } else {
            System.out.println("Payment recorded. New balance: $"
                    + String.format("%.2f", balance));
        }
    }

    public void becomeClient() {
        String clientID = getToken("Enter client ID (e.g. C1)");

        if (!context.becomeClient(clientID)) {
            System.out.println("Invalid client ID.");
        }
    }

    public void logout() {
        context.logout();
    }

    public void process() {
        help();

        while (context.getCurrentState()
                == WarehouseContext.CLERK_STATE) {
            switch (getCommand()) {
                case LOGOUT:
                    logout();
                    break;

                case ADD_CLIENT:
                    addClient();
                    break;

                case SHOW_PRODUCTS:
                    showProducts();
                    break;

                case SHOW_CLIENTS:
                    showClients();
                    break;

                case SHOW_CLIENTS_WITH_BALANCE:
                    showClientsWithBalance();
                    break;

                case RECORD_PAYMENT:
                    recordPayment();
                    break;

                case BECOME_CLIENT:
                    becomeClient();
                    break;

                case HELP:
                    help();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void run() {
        process();
    }
}
