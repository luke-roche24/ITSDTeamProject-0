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

import java.util.ArrayList;

public class BoardDemo {

    public static void createBoard(ActorRef out){
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 5; c++) {
                Tile tile = BasicObjectBuilders.loadTile(r, c);
                BasicCommands.drawTile(out, tile,0);
            }
        }
    }

}
