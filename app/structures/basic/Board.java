package structures.basic;

/** This is a simple representation of the game board with hashmap
 * containing key value pair of Coord:Tile
 */

import commands.BasicCommands;
import play.libs.F;
import utils.BasicObjectBuilders;

import java.util.HashMap;

public class Board {
    private HashMap<Coord, Tile > tiles = createBoard(9,5);

    public static HashMap<Coord, Tile> createBoard(int rows, int cols){
        HashMap<Coord, Tile > tiles = new HashMap<Coord, Tile>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Tile tile = BasicObjectBuilders.loadTile(r, c);
                Coord coord = new Coord(r, c);
                tiles.put(coord, tile);
            }
        }
        return tiles;
    }

    public HashMap<Coord, Tile> getTiles(){
        return tiles;
    }
}