package events;

import com.fasterxml.jackson.databind.JsonNode;
import akka.actor.ActorRef;
import commands.BasicCommands;
import structures.GameState;
import structures.basic.Unit;
import structures.basic.Tile;
import structures.basic.UnitAnimationType;

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

		if (isUnitOnTile(humanAvatar, tilex, tiley)) {

			// If the same unit is already selected, deselect it
			// 如果该单位已经被选中，再点击一次则取消选中
			if (gameState.selectedUnit == humanAvatar) {
				clearAdjacentHighlights(out, gameState, humanAvatar);
				gameState.selectedUnit = null;

				BasicCommands.addPlayer1Notification(
						out,
						"Unit deselected",
						2
				);

			} else {

				// Otherwise select the unit
				// 否则选中该单位
				gameState.selectedUnit = humanAvatar;

                 // Highlight adjacent movable tiles
                 // 高亮相邻可移动格子
				highlightAdjacentTiles(out, gameState, humanAvatar);

				BasicCommands.addPlayer1Notification(
						out,
						"You selected your unit at (" + tilex + ", " + tiley + ")",
						2
				);
			}

		} else if (isUnitOnTile(aiAvatar, tilex, tiley)) {

			// If a unit is selected, check whether the enemy is adjacent
			// 如果当前已经选中了一个单位，则检查敌人是否相邻
			if (gameState.selectedUnit != null) {

				int currentX = gameState.selectedUnit.getPosition().getTilex();
				int currentY = gameState.selectedUnit.getPosition().getTiley();

				int dx = Math.abs(currentX - tilex);
				int dy = Math.abs(currentY - tiley);

				// Only allow attack on adjacent enemy
				// 只允许攻击相邻的敌方单位
				if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {

					BasicCommands.playUnitAnimation(out, gameState.selectedUnit, UnitAnimationType.attack);
					BasicCommands.playUnitAnimation(out, aiAvatar, UnitAnimationType.hit);

					// Reduce enemy health by 2
					// 敌方扣除2点生命值
					int newHealth = Math.max(0, gameState.aiPlayer.getHealth() - 2);
					gameState.aiPlayer.setHealth(newHealth);

					// Update enemy health display
					// 更新敌方血量显示
					BasicCommands.setPlayer2Health(out, gameState.aiPlayer);
					BasicCommands.setUnitHealth(out, aiAvatar, gameState.aiPlayer.getHealth());
					// Remove enemy unit if health is zero or below
                    // 如果敌方血量小于等于0，则移除敌方单位
					clearAdjacentHighlights(out, gameState, gameState.selectedUnit);
					gameState.selectedUnit = null;

					if (gameState.aiPlayer.getHealth() <= 0) {
						BasicCommands.deleteUnit(out, aiAvatar);
						gameState.aiAvatar = null;
						BasicCommands.addPlayer1Notification(
								out,
								"Enemy defeated - You win!",
								2
						);
					} else {
						BasicCommands.addPlayer1Notification(
								out,
								"Attack triggered on enemy at (" + tilex + ", " + tiley + ")",
								2
						);
					}
				}

				} else {

					BasicCommands.addPlayer1Notification(
							out,
							"Invalid attack: enemy is not adjacent.",
							2
					);
				}

			} else {

				BasicCommands.addPlayer1Notification(
						out,
						"You clicked the enemy unit at (" + tilex + ", " + tiley + ")",
						2
				);
			}
		}

		} else {

			// If a unit is selected, move it to the clicked empty tile
			// 如果当前已经选中了一个单位，则将其移动到被点击的空格
			if (gameState.selectedUnit != null) {

				int currentX = gameState.selectedUnit.getPosition().getTilex();
				int currentY = gameState.selectedUnit.getPosition().getTiley();

				int dx = Math.abs(currentX - tilex);
				int dy = Math.abs(currentY - tiley);

				// Only allow movement by one tile vertically or horizontally
				// 只允许上下左右移动一格
				if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {

					Tile targetTile = gameState.board.getTiles()[tiley][tilex];
					clearAdjacentHighlights(out, gameState, gameState.selectedUnit);
					BasicCommands.moveUnitToTile(out, gameState.selectedUnit, targetTile);
					gameState.selectedUnit.setPositionByTile(targetTile);

					BasicCommands.addPlayer1Notification(
							out,
							"Unit moved to (" + tilex + ", " + tiley + ")",
							2
					);

					gameState.selectedUnit = null;

				} else {

					BasicCommands.addPlayer1Notification(
							out,
							"Invalid move: you can only move one tile.",
							2
					);
				}
			}

		    else {

				// Otherwise, the tile is empty and no unit is selected
				// 否则说明该格子为空，且当前没有选中任何单位
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
	private void highlightAdjacentTiles(ActorRef out, GameState gameState, Unit unit) {

		int currentX = unit.getPosition().getTilex();
		int currentY = unit.getPosition().getTiley();

		Tile[][] tiles = gameState.board.getTiles();

		// Up
		// 上
		if (currentY - 1 >= 0) {
			BasicCommands.drawTile(out, tiles[currentY - 1][currentX], 1);
		}

		// Down
		// 下
		if (currentY + 1 < tiles.length) {
			BasicCommands.drawTile(out, tiles[currentY + 1][currentX], 1);
		}

		// Left
		// 左
		if (currentX - 1 >= 0) {
			BasicCommands.drawTile(out, tiles[currentY][currentX - 1], 1);
		}

		// Right
		// 右
		if (currentX + 1 < tiles[0].length) {
			BasicCommands.drawTile(out, tiles[currentY][currentX + 1], 1);
		}
	}
	private void clearAdjacentHighlights(ActorRef out, GameState gameState, Unit unit) {

		int currentX = unit.getPosition().getTilex();
		int currentY = unit.getPosition().getTiley();

		Tile[][] tiles = gameState.board.getTiles();

		// Up
			if (currentY - 1 >= 0) {
				BasicCommands.drawTile(out, tiles[currentY - 1][currentX], 0);
			}

			// Down
			if (currentY + 1 < tiles.length) {
				BasicCommands.drawTile(out, tiles[currentY + 1][currentX], 0);
			}

			// Left
			if (currentX - 1 >= 0) {
				BasicCommands.drawTile(out, tiles[currentY][currentX - 1], 0);
			}

			// Right
			if (currentX + 1 < tiles[0].length) {
				BasicCommands.drawTile(out, tiles[currentY][currentX + 1], 0);
			}
		}
}