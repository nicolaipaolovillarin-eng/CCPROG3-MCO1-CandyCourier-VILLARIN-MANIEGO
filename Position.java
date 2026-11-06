/**
 * Immutable row and column coordinates in the game world.
 */
public final class Position {
    private final int row;
    private final int column;

    /**
     * Creates a position.
     *
     * @param row the zero-based row
     * @param column the zero-based column
     */
    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Returns a new position after applying a movement delta.
     *
     * @param rowDelta the change in row
     * @param columnDelta the change in column
     * @return the resulting position
     */
    public Position movedBy(int rowDelta, int columnDelta) {
        return new Position(row + rowDelta, column + columnDelta);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Position other)) {
            return false;
        }

        return row == other.row && column == other.column;
    }

    @Override
    public int hashCode() {
        return 31 * row + column;
    }
}
