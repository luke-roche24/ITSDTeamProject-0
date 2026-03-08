package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;

/**
 * Handles tile click events on the game board.
 * 处理游戏棋盘上的格子点击事件。
 *
 * This class identifies whether the clicked tile contains
 * the human player's unit, the enemy unit, or nothing.
 * 该类用于判断被点击的格子上是玩家单位、敌方单位，还是空格。
 */
public class TileClicked implements EventProcessor {

	@Override
	public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

		// Read the clicked tile coordinates from the front end
		// 从前端消息中读取被点击格子的坐标
		int tilex = message.get("tilex").asInt();
		int tiley = message.get("tiley").asInt();

		// Check whether the game state is ready
		// 检查游戏状态是否已经准备完成
		if (gameState == null || !gameState.gameInitialised) {
			BasicCommands.addPlayer1Notification(out, "Game is not ready yet.", 2);
			return;
		}

		// Get the player and AI avatar units from the game state
		// 从游戏状态中获取玩家和AI的头像单位
		Unit humanAvatar = gameState.humanAvatar;
		Unit aiAvatar = gameState.aiAvatar;

		// Check whether the clicked tile contains the human player's unit
		// 判断被点击的格子是否包含玩家单位
		if (isUnitOnTile(humanAvatar, tilex, tiley)) {
			gameState.selectedUnit = humanAvatar;
			BasicCommands.addPlayer1Notification(
					out,
					"You selected your unit at (" + tilex + ", " + tiley + ")",
					2
			);

			// Check whether the clicked tile contains the enemy unit
			// 判断被点击的格子是否包含敌方单位
		} else if (isUnitOnTile(aiAvatar, tilex, tiley)) {
			BasicCommands.addPlayer1Notification(
					out,
					"You clicked the enemy unit at (" + tilex + ", " + tiley + ")",
					2
			);

			// Otherwise, the tile is empty
			// 否则说明该格子为空
		} else {
			BasicCommands.addPlayer1Notification(
					out,
					"You clicked an empty tile at (" + tilex + ", " + tiley + ")",
					2
			);
		}
	}

	/**
	 * Checks whether a given unit is standing on the specified tile.
	 * 检查指定单位是否位于目标格子上。
	 *
	 * @param unit  the unit to check
	 *              要检查的单位
	 * @param tilex the x coordinate of the tile
	 *              格子的x坐标
	 * @param tiley the y coordinate of the tile
	 *              格子的y坐标
	 * @return true if the unit is on the tile, otherwise false
	 *         如果单位在该格子上返回true，否则返回false
	 */
	private boolean isUnitOnTile(Unit unit, int tilex, int tiley) {
		if (unit == null || unit.getPosition() == null) {
			return false;
		}

		return unit.getPosition().getTilex() == tilex
				&& unit.getPosition().getTiley() == tiley;
	}
}