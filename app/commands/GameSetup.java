package commands;

import akka.actor.ActorRef;
import structures.basic.Board;
import structures.basic.Tile;
import structures.basic.Unit;

public class GameSetup {

    public static Board drawBoard(ActorRef out){
        Board board = new Board();
        Tile[][] tiles = board.getTiles();
        for (Tile[] row : tiles){
            for (Tile tile : row) {
                BasicCommands.drawTile(out, tile, 0);
            }
        }
        return board;
    }

    public static void placeAvatar(ActorRef out, Unit unit, Board board ,int row, int col){
        Tile tile = board.getTiles()[row-1][col-1];
        unit.setPositionByTile(tile);
        BasicCommands.drawUnit(out, unit, tile);
    }
}
