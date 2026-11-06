import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores the player's candies. Each list element represents one inventory slot.
 */
public final class Inventory {
    private final List<Candy> candies = new ArrayList<>();
    private int capacity;

    /**
     * Creates an inventory with a maximum number of slots.
     *
     * @param capacity the maximum number of candies
     * @throws IllegalArgumentException if capacity is negative
     */
    public Inventory(int capacity) {
        setCapacity(capacity);
    }

    /**
     * Changes the inventory capacity.
     *
     * @param capacity the new capacity
     * @throws IllegalArgumentException if capacity is negative or below the current item count
     */
    public void setCapacity(int capacity) {
        if (capacity < 0 || capacity < candies.size()) {
            throw new IllegalArgumentException("Capacity cannot be below the current inventory count.");
        }

        this.capacity = capacity;
    }

    /**
     * Attempts to add one candy.
     *
     * @param candy the candy to add
     * @return true if the candy was added, false if the inventory is full
     */
    public boolean add(Candy candy) {
        if (candy == null || isFull()) {
            return false;
        }

        candies.add(candy);
        return true;
    }

    /**
     * Removes one matching candy.
     *
     * @param candy the candy to remove
     * @return true if a matching candy was removed
     */
    public boolean remove(Candy candy) {
        return candies.remove(candy);
    }

    public boolean isFull() {
        return candies.size() >= capacity;
    }

    public boolean contains(Candy candy) {
        return candies.contains(candy);
    }

    /**
     * Counts how many copies of a candy are currently carried.
     *
     * @param candy candy to count
     * @return number of matching candies
     */
    public int count(Candy candy) {
        if (candy == null) {
            return 0;
        }

        int count = 0;
        for (Candy item : candies) {
            if (item.equals(candy)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Removes a requested quantity of one candy.
     *
     * @param candy candy to remove
     * @param quantity number to remove
     * @return true when the requested quantity was removed
     */
    public boolean remove(Candy candy, int quantity) {
        if (candy == null || quantity < 0 || count(candy) < quantity) {
            return false;
        }

        for (int index = 0; index < quantity; index++) {
            candies.remove(candy);
        }
        return true;
    }

    /**
     * Removes one inventory slot by its one-based display number.
     *
     * @param slotNumber one-based slot number
     * @return the removed candy, or null when the slot is invalid
     */
    public Candy removeAt(int slotNumber) {
        int index = slotNumber - 1;
        if (index < 0 || index >= candies.size()) {
            return null;
        }
        return candies.remove(index);
    }

    public int getSize() {
        return candies.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getRemainingSlots() {
        return capacity - candies.size();
    }

    public List<Candy> getCandies() {
        return Collections.unmodifiableList(candies);
    }

    @Override
    public String toString() {
        return candies + " (" + candies.size() + "/" + capacity + " slots)";
    }
}
