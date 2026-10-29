import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class WarehouseInput {
    private static final BufferedReader reader =
            new BufferedReader(new InputStreamReader(System.in));

    private WarehouseInput() {
    }

    public static String getToken(String prompt) {
        while (true) {
            System.out.println(prompt);

            try {
                String line = reader.readLine();

                if (line == null) {
                    System.out.println("Input closed. Goodbye.");
                    System.exit(0);
                    return "";
                }

                line = line.trim();

                if (!line.isEmpty()) {
                    return line;
                }

                System.out.println("Please enter a value.");
            } catch (IOException exception) {
                throw new IllegalStateException(
                        "Could not read keyboard input.", exception);
            }
        }
    }

    public static int getNumber(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(getToken(prompt));
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public static double getAmount(String prompt) {
        while (true) {
            try {
                double amount = Double.parseDouble(getToken(prompt));

                if (Double.isFinite(amount)) {
                    return amount;
                }
            } catch (NumberFormatException exception) {
                // Display the message below and ask again.
            }

            System.out.println("Please enter a valid amount.");
        }
    }

    public static boolean yesOrNo(String prompt) {
        String answer = getToken(prompt + " (y/n)");
        return answer.equalsIgnoreCase("y")
                || answer.equalsIgnoreCase("yes");
    }
}