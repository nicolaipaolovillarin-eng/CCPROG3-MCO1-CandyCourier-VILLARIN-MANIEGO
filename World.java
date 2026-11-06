import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the playable square world.
 *
 * <p>The world is deliberately kept separate from {@link Player}: the world
 * validates boundaries and movement rules, while the player stores state.</p>
 */
public final class World {
    private int size;
    private Position homePosition;
    private final List<Factory> factories = new ArrayList<>();
    private final List<House> houses = new ArrayList<>();

    /**
     * Creates a world using the dimensions unlocked by a rank.
     *
     * @param rank the rank that determines the world size
     * @throws IllegalArgumentException if rank is null
     */
    public World(Rank rank) {
        if (rank == null) {
            throw new IllegalArgumentException("Rank is required.");
        }

        updateForRank(rank);
        initializeBuildings();
    }

    /**
     * Updates the world dimensions after a rank change.
     *
     * @param rank the newly unlocked rank
     */
    public void updateForRank(Rank rank) {
        if (rank == null) {
            throw new IllegalArgumentException("Rank is required.");
        }

        int newSize = rank.getWorldSize();
        if (newSize != size) {
            size = newSize;
            homePosition = new Position(size / 2, size / 2);
        }
    }

    public int getSize() {
        return size;
    }

    public Position getHomePosition() {
        return homePosition;
    }

    public List<Factory> getFactories(Rank rank) {
        List<Factory> unlocked = new ArrayList<>();
        for (Factory factory : factories) {
            if (factory.isUnlocked(rank)) {
                unlocked.add(factory);
            }
        }
        return Collections.unmodifiableList(unlocked);
    }

    public List<House> getHouses() {
        return Collections.unmodifiableList(houses);
    }

    public List<House> getHouses(Rank rank) {
        List<House> unlocked = new ArrayList<>();
        for (House house : houses) {
            if (house.isUnlocked(rank)) {
                unlocked.add(house);
            }
        }
        return Collections.unmodifiableList(unlocked);
    }

    public Factory getFactoryAt(Position position, Rank rank) {
        List<Factory> factoriesAtPosition = getFactoriesAt(position, rank);
        return factoriesAtPosition.isEmpty() ? null : factoriesAtPosition.get(0);
    }

    public List<Factory> getFactoriesAt(Position position, Rank rank) {
        List<Factory> factoriesAtPosition = new ArrayList<>();
        for (Factory factory : getFactories(rank)) {
            if (factory.getPosition().equals(position)) {
                factoriesAtPosition.add(factory);
            }
        }
        return Collections.unmodifiableList(factoriesAtPosition);
    }

    public House getHouseAt(Position position) {
        for (House house : houses) {
            if (house.getPosition().equals(position)) {
                return house;
            }
        }
        return null;
    }

    public House getHouseAt(Position position, Rank rank) {
        for (House house : getHouses(rank)) {
            if (house.getPosition().equals(position)) {
                return house;
            }
        }
        return null;
    }

    public void refreshFactories(Rank rank) {
        for (Factory factory : getFactories(rank)) {
            factory.refresh();
        }
    }

    /**
     * Checks whether a position exists inside the current world.
     *
     * @param position position to check
     * @return true when the position is within the square world
     */
    public boolean isWithinBounds(Position position) {
        if (position == null) {
            return false;
        }

        return position.getRow() >= 0
                && position.getRow() < size
                && position.getColumn() >= 0
                && position.getColumn() < size;
    }

    /**
     * Checks whether a player can make one move to a destination.
     *
     * @param player player who is moving
     * @param destination destination position
     * @return true when the move is legal
     */
    public boolean canMove(Player player, Position destination) {
        if (player == null || destination == null
                || !isWithinBounds(destination)) {
            return false;
        }

        Position current = player.getPosition();
        int rowDifference = Math.abs(destination.getRow() - current.getRow());
        int columnDifference = Math.abs(destination.getColumn() - current.getColumn());

        boolean isSingleStep = rowDifference <= 1 && columnDifference <= 1
                && (rowDifference + columnDifference > 0);
        boolean isDiagonal = rowDifference == 1 && columnDifference == 1;

        return isSingleStep && (!isDiagonal || player.getRank().allowsDiagonalMovement());
    }

    /**
     * Moves a player when the destination is legal.
     *
     * @param player player who is moving
     * @param destination destination position
     * @return true when the player moved
     */
    public boolean movePlayer(Player player, Position destination) {
        if (!canMove(player, destination)) {
            return false;
        }

        player.moveTo(destination);
        return true;
    }

    /**
     * Builds a text grid. A dot is an empty cell, H is Player Home, and P is
     * the player's current location.
     *
     * @param player player to display
     * @return the formatted world grid
     */
    public String getGrid(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player is required.");
        }

        StringBuilder grid = new StringBuilder();
        grid.append("World (").append(size).append("x").append(size)
                .append(") - P: Player, H: Player Home\n");

        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                Position cell = new Position(row, column);
                char symbol = '.';

                if (cell.equals(homePosition)) {
                    symbol = 'H';
                } else if (getFactoryAt(cell, player.getRank()) != null) {
                    symbol = 'F';
                } else if (getHouseAt(cell) != null) {
                    symbol = 'O';
                }
                if (cell.equals(player.getPosition())) {
                    symbol = 'P';
                }

                grid.append(symbol).append(' ');
            }
            grid.append("\n");
        }

        return grid.toString();
    }

    private void initializeBuildings() {
        Candy.Flavor[] commonFlavors = {
            Candy.Flavor.UBE,
            Candy.Flavor.MANGO,
            Candy.Flavor.MACAPUNO,
            Candy.Flavor.PANDAN
        };

        factories.add(new Factory(
                "Factory 1",
                new Position(0, 6),
                Rank.ZERO_STAR,
                Rank.ZERO_STAR.getFactoryGridSize(),
                101L,
                new Candy.Type[] {Candy.Type.CANDY_CORN, Candy.Type.LOLLIPOP},
                commonFlavors));
        factories.add(new Factory(
                "Factory 2",
                new Position(6, 0),
                Rank.ZERO_STAR,
                Rank.ZERO_STAR.getFactoryGridSize(),
                202L,
                new Candy.Type[] {Candy.Type.GUMMY, Candy.Type.MARSHMALLOW},
                commonFlavors));
        factories.add(new Factory(
                "Factory 3",
                new Position(0, 0),
                Rank.ONE_STAR,
                Rank.ONE_STAR.getFactoryGridSize(),
                303L,
                new Candy.Type[] {
                    Candy.Type.PASTILLAS,
                    Candy.Type.YEMA,
                    Candy.Type.POLVORON
                },
                commonFlavors));
        factories.add(new Factory(
                "Factory 4",
                new Position(10, 10),
                Rank.TWO_STAR,
                Rank.TWO_STAR.getFactoryGridSize(),
                404L,
                new Candy.Type[] {
                    Candy.Type.GUMMY,
                    Candy.Type.LOLLIPOP,
                    Candy.Type.MARSHMALLOW,
                    Candy.Type.PASTILLAS,
                    Candy.Type.YEMA,
                    Candy.Type.POLVORON
                },
                new Candy.Flavor[] {Candy.Flavor.TABLEA}));

        Candy.Flavor[] specialFlavors = {
            Candy.Flavor.JACKFRUIT,
            Candy.Flavor.DURIAN
        };
        factories.add(new Factory(
                "Factory 5",
                new Position(0, 10),
                Rank.THREE_STAR,
                Rank.THREE_STAR.getFactoryGridSize(),
                505L,
                new Candy.Type[] {
                    Candy.Type.GUMMY,
                    Candy.Type.LOLLIPOP,
                    Candy.Type.MARSHMALLOW
                },
                specialFlavors));

        Candy.Flavor[] allFlavors = {
            Candy.Flavor.UBE,
            Candy.Flavor.MANGO,
            Candy.Flavor.MACAPUNO,
            Candy.Flavor.PANDAN,
            Candy.Flavor.TABLEA,
            Candy.Flavor.JACKFRUIT,
            Candy.Flavor.DURIAN
        };
        factories.add(new Factory(
                "Factory 6",
                new Position(12, 6),
                Rank.THREE_STAR,
                Rank.THREE_STAR.getFactoryGridSize(),
                606L,
                new Candy.Type[] {Candy.Type.LOLLIPOP},
                allFlavors));
        factories.add(new Factory(
                "Factory 7",
                new Position(5, 12),
                Rank.THREE_STAR,
                Rank.THREE_STAR.getFactoryGridSize(),
                707L,
                new Candy.Type[] {Candy.Type.GUMMY},
                allFlavors));
        factories.add(new Factory(
                "Factory 8",
                new Position(6, 0),
                Rank.THREE_STAR,
                Rank.THREE_STAR.getFactoryGridSize(),
                808L,
                new Candy.Type[] {Candy.Type.MARSHMALLOW},
                allFlavors));
        factories.add(new Factory(
                "Factory 9",
                new Position(0, 0),
                Rank.FOUR_STAR,
                Rank.FOUR_STAR.getFactoryGridSize(),
                909L,
                new Candy.Type[] {
                    Candy.Type.CANDY_CORN,
                    Candy.Type.CANDY_CANE,
                    Candy.Type.PASTILLAS,
                    Candy.Type.YEMA,
                    Candy.Type.POLVORON
                },
                new Candy.Flavor[] {Candy.Flavor.UBE, Candy.Flavor.MANGO}));
        factories.add(new Factory(
                "Factory 10",
                new Position(14, 2),
                Rank.FOUR_STAR,
                Rank.FOUR_STAR.getFactoryGridSize(),
                1010L,
                new Candy.Type[] {
                    Candy.Type.PASTILLAS,
                    Candy.Type.YEMA,
                    Candy.Type.POLVORON
                },
                new Candy.Flavor[] {
                    Candy.Flavor.MACAPUNO,
                    Candy.Flavor.PANDAN,
                    Candy.Flavor.JACKFRUIT,
                    Candy.Flavor.DURIAN
                }));

        Map<Candy, Integer> firstOrderItems = new LinkedHashMap<>();
        firstOrderItems.put(new Candy(Candy.Type.CANDY_CORN), 1);
        firstOrderItems.put(new Candy(Candy.Type.GUMMY, Candy.Flavor.UBE), 1);
        houses.add(new House(
                "House 1",
                new Position(1, 5),
                new Order(firstOrderItems, 100)));

        Map<Candy, Integer> secondOrderItems = new LinkedHashMap<>();
        secondOrderItems.put(new Candy(Candy.Type.LOLLIPOP, Candy.Flavor.UBE), 1);
        secondOrderItems.put(new Candy(Candy.Type.MARSHMALLOW, Candy.Flavor.UBE), 1);
        houses.add(new House(
                "House 2",
                new Position(5, 1),
                new Order(secondOrderItems, 100)));

        Map<Candy, Integer> thirdOrderItems = new LinkedHashMap<>();
        thirdOrderItems.put(new Candy(Candy.Type.PASTILLAS, Candy.Flavor.UBE), 1);
        thirdOrderItems.put(new Candy(Candy.Type.YEMA, Candy.Flavor.UBE), 1);
        houses.add(new House(
                "House 3",
                new Position(2, 7),
                new Order(thirdOrderItems, 300),
                Rank.ONE_STAR));

        Map<Candy, Integer> fourthOrderItems = new LinkedHashMap<>();
        fourthOrderItems.put(new Candy(Candy.Type.GUMMY, Candy.Flavor.TABLEA), 1);
        fourthOrderItems.put(new Candy(Candy.Type.PASTILLAS, Candy.Flavor.TABLEA), 1);
        houses.add(new House(
                "House 4",
                new Position(9, 8),
                new Order(fourthOrderItems, 900),
                Rank.TWO_STAR));

        Map<Candy, Integer> fifthOrderItems = new LinkedHashMap<>();
        fifthOrderItems.put(new Candy(Candy.Type.GUMMY, Candy.Flavor.JACKFRUIT), 1);
        fifthOrderItems.put(new Candy(Candy.Type.LOLLIPOP, Candy.Flavor.DURIAN), 1);
        houses.add(new House(
                "House 5",
                new Position(4, 11),
                new Order(fifthOrderItems, 1600),
                Rank.THREE_STAR));

        Map<Candy, Integer> sixthOrderItems = new LinkedHashMap<>();
        sixthOrderItems.put(new Candy(Candy.Type.MARSHMALLOW, Candy.Flavor.JACKFRUIT), 1);
        sixthOrderItems.put(new Candy(Candy.Type.LOLLIPOP, Candy.Flavor.DURIAN), 1);
        houses.add(new House(
                "House 6",
                new Position(10, 5),
                new Order(sixthOrderItems, 500),
                Rank.THREE_STAR));

        Map<Candy, Integer> seventhOrderItems = new LinkedHashMap<>();
        seventhOrderItems.put(new Candy(Candy.Type.PASTILLAS, Candy.Flavor.MACAPUNO), 1);
        seventhOrderItems.put(new Candy(Candy.Type.YEMA, Candy.Flavor.JACKFRUIT), 1);
        houses.add(new House(
                "House 7",
                new Position(1, 13),
                new Order(seventhOrderItems, 1500),
                Rank.FOUR_STAR));

        Map<Candy, Integer> eighthOrderItems = new LinkedHashMap<>();
        eighthOrderItems.put(new Candy(Candy.Type.POLVORON, Candy.Flavor.PANDAN), 1);
        eighthOrderItems.put(new Candy(Candy.Type.CANDY_CANE), 1);
        houses.add(new House(
                "House 8",
                new Position(13, 3),
                new Order(eighthOrderItems, 500),
                Rank.FOUR_STAR));
    }
}
