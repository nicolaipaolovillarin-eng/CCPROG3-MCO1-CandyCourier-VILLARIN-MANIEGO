import java.util.Objects;

/**
 * Represents one candy carried by the player or placed in a factory.
 */
public final class Candy {
    /** The main kind of candy. */
    public enum Type {
        CANDY_CORN("Candy Corn"),
        CANDY_CANE("Candy Cane"),
        LOLLIPOP("Lollipop"),
        GUMMY("Gummy"),
        MARSHMALLOW("Marshmallow"),
        PASTILLAS("Pastillas"),
        YEMA("Yema"),
        POLVORON("Polvoron");

        private final String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /** The flavor of a candy, when the specification gives it one. */
    public enum Flavor {
        NONE(""),
        UBE("Ube"),
        MANGO("Mango"),
        MACAPUNO("Macapuno"),
        PANDAN("Pandan"),
        TABLEA("Tablea"),
        JACKFRUIT("Jackfruit"),
        DURIAN("Durian");

        private final String displayName;

        Flavor(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private final Type type;
    private final Flavor flavor;

    /**
     * Creates a candy without a flavor.
     *
     * @param type the candy type
     * @throws IllegalArgumentException if type is null
     */
    public Candy(Type type) {
        this(type, Flavor.NONE);
    }

    /**
     * Creates a candy with a type and flavor.
     *
     * @param type the candy type
     * @param flavor the candy flavor
     * @throws IllegalArgumentException if either argument is null
     */
    public Candy(Type type, Flavor flavor) {
        if (type == null || flavor == null) {
            throw new IllegalArgumentException("Candy type and flavor are required.");
        }

        this.type = type;
        this.flavor = flavor;
    }

    public Type getType() {
        return type;
    }

    public Flavor getFlavor() {
        return flavor;
    }

    @Override
    public String toString() {
        if (flavor == Flavor.NONE) {
            return type.toString();
        }

        return type + " (" + flavor + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Candy other)) {
            return false;
        }

        return type == other.type && flavor == other.flavor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, flavor);
    }
}
