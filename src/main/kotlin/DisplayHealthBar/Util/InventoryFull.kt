package DisplayHealthBar.Util

import org.bukkit.entity.Player

fun isInventoryFull(player: Player): Boolean
{
	return player.inventory.firstEmpty() == -1
}