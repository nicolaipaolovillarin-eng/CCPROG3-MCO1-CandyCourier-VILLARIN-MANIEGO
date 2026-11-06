import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Produces candies that the player may pick up.
 */
public final class Factory {
    private final String name;
    private final Position position;
    private final Rank introducedRank;
    private final int gridSize;
    private final List<Candy.Type> candyTypes;
    private final Set<Candy.Flavor> flavors;
    private final Random random;
    private final List<Candy> candies = new ArrayList<>();

    /**
     * Creates a factory and fills its slots for the first day.
     *
     * @param name factory name
     * @param position factory coordinates
     * @param introducedRank rank that unlocks the factory
     * @param gridSize number of rows and columns in its candy grid
     * @param seed deterministic seed for repeatable testing
     * @param candyTypes candy types produced by the factory
     * @param flavors flavors used by flavored candy types
     */
    public Factory(String name, Position position, Rank introducedRank,
                   int gridSize, long seed, Candy.Type[] candyTypes,
                   Candy.Flavor[] flavors) {
        if (name == null || name.isBlank() || position == null
                || introducedRank == null || gridSize <= 0
                || candyTypes == null || candyTypes.length == 0
                || flavors == null) {
            throw new IllegalArgumentException("Invalid factory configuration.");
        }

        this.name = name.trim();
        this.position = position;
        this.introducedRank = introducedRank;
        this.gridSize = gridSize;
        this.candyTypes = List.of(candyTypes.clone());
        this.flavors = flavors.length == 0
                ? EnumSet.noneOf(Candy.Flavor.class)
                : EnumSet.copyOf(List.of(flavors));
        this.random = new Random(seed);
        refresh();
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public Rank getIntroducedRank() {
        return introducedRank;
    }

    public int getGridSize() {
        return gridSize;
    }

    public List<Candy> getCandies() {
        return Collections.unmodifiableList(candies);
    }

    public boolean isUnlocked(Rank rank) {
        return rank != null && rank.ordinal() >= introducedRank.ordinal();
    }

    /**
     * Refills all vacant slots with a new random batch. Existing candies are
     * intentionally replaced only when the player calls this operation from
     * Player Home at the end of a day.
     */
    public void refresh() {
        candies.clear();

        // Guarantee each supported type/flavor combination so every published
        // order can be completed after a refresh.
        for (Candy.Type type : candyTypes) {
            if (type == Candy.Type.CANDY_CORN || type == Candy.Type.CANDY_CANE
                    || flavors.isEmpty()) {
                candies.add(createCandy(type, Candy.Flavor.NONE));
            } else {
                for (Candy.Flavor flavor : flavors) {
                    candies.add(createCandy(type, flavor));
                }
            }
        }

        int slotCount = gridSize * gridSize;
        while (candies.size() < slotCount) {
            Candy.Type type = candyTypes.get(random.nextInt(candyTypes.size()));
            Candy.Flavor flavor = randomFlavor();
            candies.add(createCandy(type, flavor));
        }
    }

    /**
     * Removes and returns one numbered slot. Slot numbers start at one for
     * the console menu.
     *
     * @param slotNumber one-based slot number
     * @return the candy, or null for an invalid or vacant slot
     */
    public Candy takeSlot(int slotNumber) {
        int index = slotNumber - 1;
        if (index < 0 || index >= candies.size()) {
            return null;
        }

        Candy candy = candies.get(index);
        if (candy == null) {
            return null;
        }

        // Keep the slot vacant until the next day instead of shifting candies.
        candies.set(index, null);
        return candy;
    }

    public boolean produces(Candy.Type type) {
        return type != null && candyTypes.contains(type);
    }

    public String getGridDisplay() {
        StringBuilder display = new StringBuilder();
        for (int index = 0; index < candies.size(); index++) {
            if (index % gridSize == 0) {
                display.append("\n");
            }
            display.append("[").append(index + 1).append(": ")
                    .append(candies.get(index) == null ? "Empty" : candies.get(index))
                    .append("] ");
        }
        return display.toString();
    }

    private Candy createCandy(Candy.Type type, Candy.Flavor flavor) {
        if (type == Candy.Type.CANDY_CORN || type == Candy.Type.CANDY_CANE
                || flavors.isEmpty()) {
            return new Candy(type);
        }
        return new Candy(type, flavor);
    }

    private Candy.Flavor firstFlavor() {
        if (flavors.isEmpty()) {
            return Candy.Flavor.NONE;
        }
        return flavors.iterator().next();
    }

    private Candy.Flavor randomFlavor() {
        if (flavors.isEmpty()) {
            return Candy.Flavor.NONE;
        }

        List<Candy.Flavor> flavorList = new ArrayList<>(flavors);
        return flavorList.get(random.nextInt(flavorList.size()));
    }
}
