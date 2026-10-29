public class WarehouseContext {
    private static WarehouseContext instance;

    public static final int LOGIN_STATE = 0;
    public static final int CLIENT_STATE = 1;
    public static final int CLERK_STATE = 2;
    public static final int MANAGER_STATE = 3;

    private static final int NO_ROLE = -1;
    private static final int INVALID_TRANSITION = -1;

    private static final int LOGIN_CLIENT = 0;
    private static final int LOGIN_CLERK = 1;
    private static final int LOGIN_MANAGER = 2;
    private static final int BECOME_CLIENT = 3;
    private static final int BECOME_CLERK = 4;
    private static final int LOGOUT_TO_LOGIN = 5;
    private static final int LOGOUT_TO_CLERK = 6;
    private static final int LOGOUT_TO_MANAGER = 7;

    private final Warehouse warehouse;
    private WarehouseState[] states;

    private int currentState = LOGIN_STATE;
    private int originalLoginRole = NO_ROLE;
    private String activeClientID;
    private boolean running = true;

    // Rows: events. Columns: Login, Client, Clerk, Manager.
    private final int[][] transitionMatrix = {
        {CLIENT_STATE, -1, -1, -1},
        {CLERK_STATE, -1, -1, -1},
        {MANAGER_STATE, -1, -1, -1},
        {-1, -1, CLIENT_STATE, -1},
        {-1, -1, -1, CLERK_STATE},
        {-1, LOGIN_STATE, LOGIN_STATE, LOGIN_STATE},
        {-1, CLERK_STATE, -1, -1},
        {-1, -1, MANAGER_STATE, -1}
    };

    private WarehouseContext() {
        warehouse = Warehouse.instance();
    }

    public static WarehouseContext instance() {
        if (instance == null) {
            instance = new WarehouseContext();
        }
        return instance;
    }

    public void process() {
        // Initialize states after the Context singleton exists.
        states = new WarehouseState[] {
            LoginState.instance(),
            ClientMenuState.instance(),
            ClerkMenuState.instance(),
            ManagerMenuState.instance()
        };

        while (running) {
            states[currentState].run();
        }
    }

    public boolean login(int role, String clientID) {
        if (currentState != LOGIN_STATE) {
            return false;
        }

        int event;
        String verifiedClientID = null;

        switch (role) {
            case CLIENT_STATE:
                if (clientID == null) {
                    return false;
                }

                verifiedClientID = clientID.trim();

                if (warehouse.getClient(verifiedClientID) == null) {
                    return false;
                }

                event = LOGIN_CLIENT;
                break;

            case CLERK_STATE:
                event = LOGIN_CLERK;
                break;

            case MANAGER_STATE:
                event = LOGIN_MANAGER;
                break;

            default:
                return false;
        }

        originalLoginRole = role;
        activeClientID = verifiedClientID;
        changeState(event);
        return true;
    }

    public boolean becomeClient(String clientID) {
        if (currentState != CLERK_STATE || clientID == null) {
            return false;
        }

        String verifiedClientID = clientID.trim();

        if (warehouse.getClient(verifiedClientID) == null) {
            return false;
        }

        activeClientID = verifiedClientID;
        changeState(BECOME_CLIENT);
        return true;
    }

    public boolean becomeClerk() {
        if (currentState != MANAGER_STATE) {
            return false;
        }

        changeState(BECOME_CLERK);
        return true;
    }

    public void logout() {
        switch (currentState) {
            case CLIENT_STATE:
                activeClientID = null;

                if (originalLoginRole == CLIENT_STATE) {
                    changeState(LOGOUT_TO_LOGIN);
                } else {
                    changeState(LOGOUT_TO_CLERK);
                }
                break;

            case CLERK_STATE:
                if (originalLoginRole == MANAGER_STATE) {
                    changeState(LOGOUT_TO_MANAGER);
                } else {
                    changeState(LOGOUT_TO_LOGIN);
                }
                break;

            case MANAGER_STATE:
                changeState(LOGOUT_TO_LOGIN);
                break;

            default:
                return;
        }

        if (currentState == LOGIN_STATE) {
            clearSession();
        }
    }

    public String getActiveClientID() {
        return activeClientID;
    }

    public int getOriginalLoginRole() {
        return originalLoginRole;
    }

    public int getCurrentState() {
        return currentState;
    }

    private void changeState(int event) {
        if (event < 0 || event >= transitionMatrix.length) {
            throw new IllegalArgumentException("Unknown transition event.");
        }

        int nextState = transitionMatrix[event][currentState];

        if (nextState == INVALID_TRANSITION) {
            throw new IllegalStateException(
                    "Transition is not allowed from the current state.");
        }

        currentState = nextState;
    }

    private void clearSession() {
        originalLoginRole = NO_ROLE;
        activeClientID = null;
    }

    public void stop() {
        running = false;
        clearSession();
        currentState = LOGIN_STATE;
    }

    public static void main(String[] args) {
        WarehouseContext.instance().process();
    }
}