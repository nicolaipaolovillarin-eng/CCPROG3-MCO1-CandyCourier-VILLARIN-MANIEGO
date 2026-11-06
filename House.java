/**
 * A world building containing one delivery order.
 */
public final class House {
    private final String name;
    private final Position position;
    private final Order order;
    private final Rank introducedRank;
    private boolean delivered;

    public House(String name, Position position, Order order) {
        this(name, position, order, Rank.ZERO_STAR);
    }

    public House(String name, Position position, Order order, Rank introducedRank) {
        if (name == null || name.isBlank() || position == null || order == null) {
            throw new IllegalArgumentException("Invalid house configuration.");
        }
        if (introducedRank == null) {
            throw new IllegalArgumentException("House rank is required.");
        }

        this.name = name.trim();
        this.position = position;
        this.order = order;
        this.introducedRank = introducedRank;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    public Order getOrder() {
        return order;
    }

    public Rank getIntroducedRank() {
        return introducedRank;
    }

    public boolean isUnlocked(Rank rank) {
        return rank != null && rank.ordinal() >= introducedRank.ordinal();
    }

    public boolean isDelivered() {
        return delivered;
    }

    public boolean deliver(Player player) {
        if (delivered || !order.fulfill(player)) {
            return false;
        }

        delivered = true;
        return true;
    }
}
