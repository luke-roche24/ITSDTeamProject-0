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
import commands.GameSetup;
import java.util.HashMap;
import java.util.Map;
import structures.GameState;

class GameEngine {

    public static void startGame(ActorRef out,GameState gameState) {

        // Draws the board
        Board board = GameSetup.drawBoard(out);
        Tile[][] tiles = board.getTiles();
        // Store the board in the global game state
        // 将棋盘对象保存到全局游戏状态中
        gameState.board = board;
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}


        // Set players health
        Player humanPlayer = new Player(20, 0);
        BasicCommands.setPlayer1Health(out, humanPlayer);
        Player aiPlayer = new Player(20, 0);
        BasicCommands.setPlayer2Health(out, aiPlayer);
        gameState.humanPlayer = humanPlayer;
        gameState.aiPlayer = aiPlayer;
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}

        // Place avatar units
        Unit humanAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.humanAvatar, 0, Unit.class);
        Unit aiAvatar = BasicObjectBuilders.loadUnit(StaticConfFiles.aiAvatar, 1, Unit.class);
        GameSetup.placeAvatar(out, humanAvatar, board, 3, 2);
        GameSetup.placeAvatar(out, aiAvatar, board, 3, 8);
        // Store avatar units in the global game state
        // 将双方头像单位保存到全局游戏状态中
        gameState.humanAvatar = humanAvatar;
        gameState.aiAvatar = aiAvatar;
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}



        BasicCommands.setUnitHealth(out, humanAvatar, humanPlayer.getHealth());
        BasicCommands.setUnitHealth(out, aiAvatar, aiPlayer.getHealth());
        try {Thread.sleep(2000);} catch (InterruptedException e) {e.printStackTrace();}
        // Mark the game as fully initialised
        // 将游戏标记为已完成初始化
        gameState.gameInitalised = true;

    }
}
