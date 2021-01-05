package DisplayHealthBar


import DisplayHealthBar.Event.EntityBossBarHealthEvents
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.lang.Exception

class CommandManager(private var plugin: Main) : CommandExecutor {  //todo 빌드해서 확인해보자.
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if(command.name == "healthBar") {
			when {
//				args[0].equals("addAllPlayers", true) -> {  //작동안함
//					val entityBossBarHealthEvents = EntityBossBarHealthEvents()
//					for(player in Bukkit.getOnlinePlayers()) {
//						entityBossBarHealthEvents.playerList[player.name] = player
//						entityBossBarHealthEvents.bossBarList[player.name] = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)
//						entityBossBarHealthEvents.bossBarList[player.name]!!.addPlayer(player)
//						entityBossBarHealthEvents.bossBarList[player.name]!!.isVisible = false
//					}
//				}
//				args[0].equals("delAllPlayers", true) -> {
//					val entityBossBarHealthEvents = EntityBossBarHealthEvents()
//					for(player in Bukkit.getOnlinePlayers()) {
//						try {
//							entityBossBarHealthEvents.bossBarList[player.name]!!.removePlayer(player)
//							entityBossBarHealthEvents.bossBarList.remove(player.name, entityBossBarHealthEvents.bossBarList[player.name]!!)
//						}
//						catch(exception: Exception) {
//							println("bossBar를 찾을 수 없습니다.")
//						}
//					}
//				}
				args[0] == "getOnlinePlayers" -> {
					var count = 0
					for(player in Bukkit.getOnlinePlayers()) {
						println(player.name)
						count++
					}
					println("Player Count : $count")
				}
			}
		}

		return true
	}
}