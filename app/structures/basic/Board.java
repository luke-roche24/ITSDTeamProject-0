package structures.basic;

/** This is a simple representation of the game board with hashmap
 * containing key value pair of Coord:Tile
 */

import commands.BasicCommands;
import play.libs.F;
import utils.BasicObjectBuilders;

import java.util.HashMap;

public class Board {
    private Tile[][] tiles = createBoard(5,9);

    public static Tile[][] createBoard(int rows, int cols){
        //HashMap<Coord, Tile > tiles = new HashMap<Coord, Tile>();
        Tile[][] tiles = new Tile[5][9];
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) {
                Tile tile = BasicObjectBuilders.loadTile(c, r);
                tiles[r][c] = tile;
            }
        }
        return tiles;
    }

    public Tile[][] getTiles(){
        return tiles;
    }
}