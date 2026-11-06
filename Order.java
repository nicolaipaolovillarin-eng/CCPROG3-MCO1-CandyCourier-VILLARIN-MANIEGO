import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A delivery request made by a house.
 */
public final class Order {
    private final Map<Candy, Integer> requirements;
    private final int rewardXP;

    /**
     * Creates an order.
     *
     * @param requirements required candy quantities
     * @param rewardXP XP awarded on delivery
     */
    public Order(Map<Candy, Integer> requirements, int rewardXP) {
        if (requirements == null || requirements.isEmpty() || rewardXP < 0) {
            throw new IllegalArgumentException("Invalid order.");
        }

        this.requirements = new LinkedHashMap<>();
        for (Map.Entry<Candy, Integer> entry : requirements.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null
                    || entry.getValue() <= 0) {
                throw new IllegalArgumentException("Order quantities must be positive.");
            }
            this.requirements.put(entry.getKey(), entry.getValue());
        }
        this.rewardXP = rewardXP;
    }

    public Map<Candy, Integer> getRequirements() {
        return Collections.unmodifiableMap(requirements);
    }

    public int getRewardXP() {
        return rewardXP;
    }

    public boolean canFulfill(Inventory inventory) {
        if (inventory == null) {
            return false;
        }

        for (Map.Entry<Candy, Integer> entry : requirements.entrySet()) {
            if (inventory.count(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Describes the missing items without changing the inventory.
     */
    public String getMissingItems(Inventory inventory) {
        if (inventory == null) {
            return "Inventory unavailable";
        }

        StringBuilder missing = new StringBuilder();
        for (Map.Entry<Candy, Integer> entry : requirements.entrySet()) {
            int missingCount = entry.getValue() - inventory.count(entry.getKey());
            if (missingCount > 0) {
                if (missing.length() > 0) {
                    missing.append(", ");
                }
                missing.append(missingCount).append(" x ").append(entry.getKey());
            }
        }
        return missing.length() == 0 ? "None" : missing.toString();
    }

    /**
     * Removes all required candies and awards XP when possible.
     *
     * @param player player making the delivery
     * @return true when the order was fulfilled
     */
    public boolean fulfill(Player player) {
        if (player == null || !canFulfill(player.getInventory())) {
            return false;
        }

        for (Map.Entry<Candy, Integer> entry : requirements.entrySet()) {
            player.getInventory().remove(entry.getKey(), entry.getValue());
        }
        player.addXP(rewardXP);
        return true;
    }

    @Override
    public String toString() {
        StringBuilder summary = new StringBuilder();
        for (Map.Entry<Candy, Integer> entry : requirements.entrySet()) {
            if (summary.length() > 0) {
                summary.append(", ");
            }
            summary.append(entry.getValue()).append(" x ").append(entry.getKey());
        }
        return summary.toString();
    }
}
