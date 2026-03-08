package structures;

/**
 * This class can be used to hold information about the on-going game.
 * Its created with the GameActor.
 * 
 * @author Dr. Richard McCreadie
 *
 */

import structures.basic.Board;
import structures.basic.Unit;

/**
 * GameState stores the global state of the game.
 * GameState 用于存储整个游戏运行过程中的全局状态。
 */
public class GameState {

	// Indicates whether the game has been initialised
	// 表示游戏是否已经初始化完成
	public boolean gameInitalised = false;

	// Temporary variable used in the template (may be removed later)
	// 模板中的临时变量（后期可能会删除）
	public boolean something = true;

	// The game board containing all tiles
	// 游戏棋盘，包含所有的Tile格子
	public Board board;

	// The human player's avatar unit
	// 玩家控制的英雄单位
	public Unit humanAvatar;

	// The AI player's avatar unit
	// AI控制的英雄单位
	public Unit aiAvatar;

	// The unit currently selected by the player
	// 当前被玩家选中的单位
	public Unit selectedUnit;
}
