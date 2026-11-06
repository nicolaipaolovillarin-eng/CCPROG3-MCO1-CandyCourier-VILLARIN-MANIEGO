/**
 * Star ranks and the features unlocked at each rank.
 */
public enum Rank {
    ZERO_STAR(0, 0, 7, 6, 20),
    ONE_STAR(1, 100, 9, 7, 40),
    TWO_STAR(2, 500, 11, 8, 60),
    THREE_STAR(3, 1400, 13, 9, 80),
    FOUR_STAR(4, 3000, 15, 10, 100),
    FIVE_STAR(5, 5500, 15, 10, 100);

    private final int stars;
    private final int requiredXP;
    private final int worldSize;
    private final int factoryGridSize;
    private final int inventoryCapacity;

    Rank(int stars, int requiredXP, int worldSize,
         int factoryGridSize, int inventoryCapacity) {
        this.stars = stars;
        this.requiredXP = requiredXP;
        this.worldSize = worldSize;
        this.factoryGridSize = factoryGridSize;
        this.inventoryCapacity = inventoryCapacity;
    }

    /**
     * Finds the highest rank unlocked by the supplied XP.
     *
     * @param totalXP the player's total XP
     * @return the player's current rank
     */
    public static Rank fromXP(int totalXP) {
        Rank currentRank = ZERO_STAR;

        for (Rank rank : values()) {
            if (totalXP >= rank.requiredXP) {
                currentRank = rank;
            }
        }

        return currentRank;
    }

    public int getStars() {
        return stars;
    }

    public int getRequiredXP() {
        return requiredXP;
    }

    public int getWorldSize() {
        return worldSize;
    }

    public int getFactoryGridSize() {
        return factoryGridSize;
    }

    public int getInventoryCapacity() {
        return inventoryCapacity;
    }

    public boolean allowsDiagonalMovement() {
        return stars >= THREE_STAR.stars;
    }

    @Override
    public String toString() {
        return stars + "-star";
    }
}
