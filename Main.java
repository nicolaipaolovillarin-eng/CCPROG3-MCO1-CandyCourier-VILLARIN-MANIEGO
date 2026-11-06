import java.util.Scanner;

/**
 * Entry point for the Candy Courier console application.
 */
public final class Main {
    private Main() {
        // Utility class; do not instantiate.
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the player's name: ");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            name = "Player";
        }

        new Game(scanner, name).run();
    }
}
