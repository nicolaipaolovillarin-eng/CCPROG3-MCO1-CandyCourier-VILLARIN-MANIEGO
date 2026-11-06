import java.util.Scanner;

/**
 * Controls the console game loop and the first playable MCO1 features.
 */
public final class Game {
    private final Scanner scanner;
    private final Player player;
    private final World world;
    private boolean running;

    /**
     * Creates a game with a new zero-star player.
     *
     * @param scanner input source
     * @param playerName player name
     */
    public Game(Scanner scanner, String playerName) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner is required.");
        }
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name is required.");
        }

        this.scanner = scanner;
        int startingCoordinate = Rank.ZERO_STAR.getWorldSize() / 2;
        this.player = new Player(playerName, new Position(startingCoordinate, startingCoordinate));
        this.world = new World(player.getRank());
    }

    /**
     * Runs the game until the player selects Quit or input ends.
     */
    public void run() {
        running = true;
        printWelcome();

        while (running) {
            world.updateForRank(player.getRank());
            System.out.println(world.getGrid(player));
            printMenu();

            String choice = readLine();
            if (choice == null) {
                quit();
                return;
            }

            switch (choice.trim()) {
                case "1" -> travel();
                case "2" -> searchCandies();
                case "3" -> viewOrders();
                case "4" -> inventoryMenu();
                case "5" -> System.out.println("\n" + player.getStatistics());
                case "6" -> quit();
                default -> System.out.println("\nInvalid choice. Please select 1 to 6.");
            }

            checkForGameCompletion();
        }
    }

    private void printWelcome() {
        System.out.println("\n=== Candy Courier ===");
        System.out.println("Starting a new game...");
        System.out.println(player.getStatistics());
    }

    private void printMenu() {
        System.out.println("Menu:");
        System.out.println("1. Travel");
        System.out.println("2. Search Candies");
        System.out.println("3. View Orders");
        System.out.println("4. View Inventory");
        System.out.println("5. View Player Info");
        System.out.println("6. Quit");
        System.out.print("Choose an option: ");
    }

    private void travel() {
        System.out.println("\nTravel mode. Enter up, down, left, right, up-left, up-right,"
                + " down-left, down-right, or back.");

        while (true) {
            System.out.print("Direction: ");
            String direction = readLine();
            if (direction == null || direction.equalsIgnoreCase("back")
                    || direction.equalsIgnoreCase("exit")) {
                return;
            }

            int rowDelta = 0;
            int columnDelta = 0;
            switch (direction.trim().toLowerCase()) {
                case "up" -> rowDelta = -1;
                case "down" -> rowDelta = 1;
                case "left" -> columnDelta = -1;
                case "right" -> columnDelta = 1;
                case "up-left", "upleft" -> {
                    rowDelta = -1;
                    columnDelta = -1;
                }
                case "up-right", "upright" -> {
                    rowDelta = -1;
                    columnDelta = 1;
                }
                case "down-left", "downleft" -> {
                    rowDelta = 1;
                    columnDelta = -1;
                }
                case "down-right", "downright" -> {
                    rowDelta = 1;
                    columnDelta = 1;
                }
                default -> {
                    System.out.println("Invalid direction. Use a listed direction or back.");
                    continue;
                }
            }

            Position destination = player.getPosition().movedBy(rowDelta, columnDelta);
            if (!world.isWithinBounds(destination)) {
                System.out.println("You cannot move outside the world.");
            } else if (!world.canMove(player, destination)) {
                System.out.println("That move is not available at your current rank.");
            } else {
                world.movePlayer(player, destination);
                System.out.println("Moved to " + player.getPosition()
                        + ". Total steps: " + player.getTotalSteps());
                System.out.println(world.getGrid(player));
                interactAtCurrentLocation();
                if (checkForGameCompletion()) {
                    return;
                }
            }
        }
    }

    private void searchCandies() {
        System.out.println("\nAvailable candy types:");
        for (Candy.Type type : Candy.Type.values()) {
            System.out.println("- " + type);
        }
        System.out.print("Enter a candy type: ");
        String search = readLine();
        if (search == null) {
            return;
        }

        Candy.Type type = findCandyType(search);
        if (type == null) {
            System.out.println("Candy type not found.");
            return;
        }

        System.out.println("Factories producing " + type + ":");
        boolean found = false;
        for (Factory factory : world.getFactories(player.getRank())) {
            if (factory.produces(type)) {
                System.out.println("- " + factory.getName() + " at " + factory.getPosition());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No unlocked factory currently produces that candy.");
        }
    }

    private void viewOrders() {
        System.out.println("\nOrders:");
        for (House house : world.getHouses(player.getRank())) {
            String status = house.isDelivered() ? "DELIVERED" : "OPEN";
            System.out.println(house.getName() + " at " + house.getPosition()
                    + " - " + status);
            System.out.println("  Needs: " + house.getOrder());
            System.out.println("  Reward: " + house.getOrder().getRewardXP() + " XP");
        }
    }

    private void interactAtCurrentLocation() {
        java.util.List<Factory> factories = world.getFactoriesAt(
                player.getPosition(), player.getRank());
        if (!factories.isEmpty()) {
            for (Factory factory : factories) {
                visitFactory(factory);
            }
            return;
        }

        House house = world.getHouseAt(player.getPosition(), player.getRank());
        if (house != null) {
            visitHouse(house);
            return;
        }

        if (player.getPosition().equals(world.getHomePosition())) {
            visitPlayerHome();
        }
    }

    private boolean checkForGameCompletion() {
        if (player.getRank() != Rank.FIVE_STAR) {
            return false;
        }

        for (House house : world.getHouses(player.getRank())) {
            if (!house.isDelivered()) {
                return false;
            }
        }

        running = false;
        System.out.println("\n=== Congratulations! ===");
        System.out.println("You reached 5-star rank and completed every delivery.");
        System.out.println("Final Player Statistics:");
        System.out.println(player.getStatistics());
        return true;
    }

    private void visitFactory(Factory factory) {
        System.out.println("\nEntering " + factory.getName() + " at " + factory.getPosition());
        System.out.println("Select a slot to pick up a candy, or enter 0 to exit.");

        while (true) {
            System.out.println(factory.getGridDisplay());
            System.out.print("Slot: ");
            String input = readLine();
            if (input == null || input.trim().equals("0")) {
                return;
            }

            int slot;
            try {
                slot = Integer.parseInt(input.trim());
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid slot number.");
                continue;
            }

            if (player.getInventory().isFull()) {
                System.out.println("Inventory is full. Discard a candy before picking up another.");
                continue;
            }

            Candy candy = factory.takeSlot(slot);
            if (candy == null) {
                System.out.println("That slot is empty or invalid.");
            } else {
                player.getInventory().add(candy);
                System.out.println("Picked up " + candy + ".");
            }
        }
    }

    private void visitHouse(House house) {
        System.out.println("\n" + house.getName() + " at " + house.getPosition());
        if (house.isDelivered()) {
            System.out.println("This order has already been delivered.");
            return;
        }

        System.out.println("Order: " + house.getOrder());
        if (!house.getOrder().canFulfill(player.getInventory())) {
            System.out.println("Insufficient candies. Missing: "
                    + house.getOrder().getMissingItems(player.getInventory()));
            return;
        }

        System.out.print("Deliver these candies? (yes/no): ");
        String answer = readLine();
        if (answer != null && answer.trim().equalsIgnoreCase("yes")) {
            if (house.deliver(player)) {
                System.out.println("Delivery complete! Earned "
                        + house.getOrder().getRewardXP() + " XP.");
            }
        } else {
            System.out.println("Delivery cancelled.");
        }
    }

    private void visitPlayerHome() {
        System.out.print("You are at Player Home. Call it a day? (yes/no): ");
        String answer = readLine();
        if (answer != null && answer.trim().equalsIgnoreCase("yes")) {
            world.refreshFactories(player.getRank());
            player.advanceDay();
            System.out.println("A new day begins. Factory candies have been refreshed.");
        } else {
            System.out.println("You continue the current day.");
        }
    }

    private void inventoryMenu() {
        while (true) {
            System.out.println("\nInventory: " + player.getInventory());
            if (player.getInventory().getSize() == 0) {
                System.out.println("Inventory is empty.");
                return;
            }

            System.out.println("Slots:");
            for (int index = 0; index < player.getInventory().getCandies().size(); index++) {
                System.out.println((index + 1) + ". "
                        + player.getInventory().getCandies().get(index));
            }
            System.out.print("Enter slot numbers to discard (example: 1,3), or back: ");
            String input = readLine();
            if (input == null || input.trim().equalsIgnoreCase("back")) {
                return;
            }

            String[] slotTokens = input.trim().split("[,\\s]+");
            java.util.List<Integer> slots = new java.util.ArrayList<>();
            boolean validInput = true;
            for (String token : slotTokens) {
                try {
                    slots.add(Integer.parseInt(token));
                } catch (NumberFormatException exception) {
                    validInput = false;
                    break;
                }
            }

            if (!validInput || slots.isEmpty()) {
                System.out.println("Enter valid slot numbers separated by commas or spaces.");
                continue;
            }

            slots.sort(java.util.Collections.reverseOrder());
            int discarded = 0;
            for (int slot : slots) {
                Candy candy = player.getInventory().removeAt(slot);
                if (candy != null) {
                    System.out.println("Discarded " + candy + ".");
                    discarded++;
                } else {
                    System.out.println("Slot " + slot + " is invalid.");
                }
            }
            System.out.println("Discarded " + discarded + " item(s).");
        }
    }

    private Candy.Type findCandyType(String search) {
        String normalized = search.trim().replace(" ", "_").toUpperCase();
        for (Candy.Type type : Candy.Type.values()) {
            if (type.name().equals(normalized)
                    || type.toString().equalsIgnoreCase(search.trim())) {
                return type;
            }
        }
        return null;
    }

    private void quit() {
        running = false;
        System.out.println("\n=== Final Player Statistics ===");
        System.out.println(player.getStatistics());
    }

    private String readLine() {
        if (!scanner.hasNextLine()) {
            return null;
        }
        return scanner.nextLine();
    }
}
