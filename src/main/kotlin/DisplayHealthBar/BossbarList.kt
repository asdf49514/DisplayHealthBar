package DisplayHealthBar

import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.HashMap

object BossbarList
{
	var playerList = HashMap<String, Player>()
	var bossBarList = HashMap<String, BossBar>()
	var taskID = HashMap<String, Int>()
}