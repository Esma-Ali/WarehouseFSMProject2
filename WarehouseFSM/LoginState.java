public class LoginState extends WarehouseState {
    private static LoginState instance;

    private static final int EXIT = 0;
    private static final int CLIENT = 1;
    private static final int CLERK = 2;
    private static final int MANAGER = 3;
    private static final int HELP = 4;

    private LoginState() {
        super();
    }

    public static LoginState instance() {
        if (instance == null) {
            instance = new LoginState();
        }
        return instance;
    }

    public void help() {
        System.out.println("\n---- Warehouse Login ----");
        System.out.println("0 to exit");
        System.out.println("1 to login as a client");
        System.out.println("2 to login as a clerk");
        System.out.println("3 to login as a manager");
        System.out.println("4 for help");
    }

    public int getCommand() {
        while (true) {
            int command = WarehouseInput.getNumber("Enter command");

            if (command >= EXIT && command <= HELP) {
                return command;
            }

            System.out.println("Enter a number between 0 and 4.");
        }
    }

    public void loginClient() {
        String clientID = WarehouseInput.getToken(
                "Enter client ID (e.g. C1)");

        boolean success = WarehouseContext.instance().login(
                WarehouseContext.CLIENT_STATE, clientID);

        if (!success) {
            System.out.println("Client ID not found.");
        }
    }

    public void loginClerk() {
        if (!WarehouseContext.instance().login(
                WarehouseContext.CLERK_STATE, null)) {
            System.out.println("Could not login as clerk.");
        }
    }

    public void loginManager() {
        if (!WarehouseContext.instance().login(
                WarehouseContext.MANAGER_STATE, null)) {
            System.out.println("Could not login as manager.");
        }
    }

    @Override
    public void run() {
        WarehouseContext context = WarehouseContext.instance();
        help();

        while (context.getCurrentState()
                == WarehouseContext.LOGIN_STATE) {
            switch (getCommand()) {
                case EXIT:
                    context.stop();
                    System.out.println("Goodbye.");
                    return;

                case CLIENT:
                    loginClient();
                    break;

                case CLERK:
                    loginClerk();
                    break;

                case MANAGER:
                    loginManager();
                    break;

                case HELP:
                    help();
                    break;

                default:
                    break;
            }
        }
    }
}