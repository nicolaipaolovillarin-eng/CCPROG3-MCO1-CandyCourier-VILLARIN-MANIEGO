/**
 * Stores the state of the Candy Courier player.
 */
public final class Player {
    private final String name;
    private Position position;
    private final Inventory inventory;
    private int totalXP;
    private int dayNumber;
    private int totalSteps;

    /**
     * Creates a new player at the specified starting position.
     *
     * @param name the player's name
     * @param startingPosition the player's starting position
     * @throws IllegalArgumentException if the name or position is invalid
     */
    public Player(String name, Position startingPosition) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Player name is required.");
        }
        if (startingPosition == null) {
            throw new IllegalArgumentException("Starting position is required.");
        }

        this.name = name.trim();
        this.position = startingPosition;
        this.inventory = new Inventory(Rank.ZERO_STAR.getInventoryCapacity());
        this.dayNumber = 1;
    }

    /**
     * Adds XP and updates the player's rank and inventory capacity.
     *
     * @param xp the XP to add
     * @throws IllegalArgumentException if xp is negative
     */
    public void addXP(int xp) {
        if (xp < 0) {
            throw new IllegalArgumentException("XP cannot be negative.");
        }

        totalXP += xp;
        inventory.setCapacity(getRank().getInventoryCapacity());
    }

    /**
     * Moves the player and records one step.
     * Boundary checks will be handled by the World class.
     *
     * @param newPosition the destination position
     * @throws IllegalArgumentException if newPosition is null
     */
    public void moveTo(Position newPosition) {
        if (newPosition == null) {
            throw new IllegalArgumentException("Destination position is required.");
        }

        position = newPosition;
        totalSteps++;
    }

    public void advanceDay() {
        dayNumber++;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getTotalXP() {
        return totalXP;
    }

    public Rank getRank() {
        return Rank.fromXP(totalXP);
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public int getXPToNextRank() {
        Rank currentRank = getRank();
        if (currentRank == Rank.FIVE_STAR) {
            return 0;
        }

        Rank nextRank = Rank.values()[currentRank.ordinal() + 1];
        return nextRank.getRequiredXP() - totalXP;
    }

    /**
     * Returns the statistics required by the MCO1 quit and player-info features.
     *
     * @return a formatted statistics summary
     */
    public String getStatistics() {
        return "Player: " + name
                + "\nRank: " + getRank()
                + "\nTotal XP: " + totalXP
                + "\nXP to next rank: " + getXPToNextRank()
                + "\nTotal steps: " + totalSteps
                + "\nDay: " + dayNumber
                + "\nInventory: " + inventory;
    }
}
