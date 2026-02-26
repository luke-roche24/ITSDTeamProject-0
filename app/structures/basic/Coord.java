package structures.basic;

/** This is a simple class to represent coordinate
 * object to hash with each tile on the board
 */

public final class Coord {
    private final int row;
    private final int col;

    public Coord(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int row() {return row;}
    public int col() {return col;}

}
