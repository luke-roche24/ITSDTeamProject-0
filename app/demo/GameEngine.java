package demo;

import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.basic.Card;
import structures.basic.EffectAnimation;
import structures.basic.Player;
import structures.basic.Tile;
import structures.basic.Unit;
import structures.basic.UnitAnimationType;
import utils.BasicObjectBuilders;
import utils.OrderedCardLoader;
import utils.StaticConfFiles;
import demo.BoardDemo;
import structures.basic.Board;
import structures.basic.Coord;

import java.util.HashMap;
import java.util.Map;

public class GameEngine {

    public static void startGame(ActorRef out) {

        // This can definitely be made into a basic command
        Board board = new Board();
        HashMap<Coord, Tile> tiles = board.getTiles();
        for (Tile tile : tiles.values()){
            BasicCommands.drawTile(out, tile, 0);
        }

        // Set player 1 health
        Player humanPlayer = new Player(20, 0);
        BasicCommands.setPlayer1Health(out, humanPlayer);
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

        // Set player 2 health
        Player aiPlayer = new Player(20, 0);
        BasicCommands.setPlayer2Health(out, aiPlayer);
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
