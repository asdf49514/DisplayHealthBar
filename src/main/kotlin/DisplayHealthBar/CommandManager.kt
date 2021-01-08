package DisplayHealthBar


import DisplayHealthBar.Event.EntityBossBarHealthEvents
import DisplayHealthBar.Settings.Settings
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import java.lang.Exception
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException
import java.lang.NumberFormatException

class CommandManager(private var plugin: Main) : CommandExecutor {  //todo 빌드해서 확인해보자.
	override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
		if(command.name == "healthBar") {
			when {
				args[0].equals("reloadAll", true) -> {  //작동안함
					for(player in Bukkit.getOnlinePlayers()) {
						try {
							BossbarList.bossBarList[player.name]!!.removePlayer(player)
							BossbarList.bossBarList.remove(player.name, BossbarList.bossBarList[player.name]!!)
							BossbarList.taskID.remove(player.name)
						} catch(ignored: Exception) { }
						
						BossbarList.playerList[player.name] = player
						BossbarList.bossBarList[player.name] = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)
						BossbarList.bossBarList[player.name]!!.addPlayer(player)
						BossbarList.bossBarList[player.name]!!.isVisible = false
						BossbarList.taskID[player.name] = Math.random().toInt()
					}
				}
				args[0].equals("deleteAll", true) -> {
					for(player in Bukkit.getOnlinePlayers()) {
						try {
							BossbarList.bossBarList[player.name]!!.removePlayer(player)
							BossbarList.bossBarList.remove(player.name, BossbarList.bossBarList[player.name]!!)
							BossbarList.taskID.remove(player.name)
						}
						catch(exception: Exception) {
							println("${player.name}의 bossBar를 찾을 수 없습니다. (deleteAll)")
						}
					}
				}
				args[0] == "getOnlinePlayers" -> {
					var count = 0
					for(player in Bukkit.getOnlinePlayers()) {
						println(player.name)
						count++
					}
					println("Player Count : $count")
				}
				args[0] == "setDeltaVelocity" -> {
					val warning = "${ChatColor.RED}0.1 ~ 1.0 사이의 자연수를 입력해 주세요."
					
					val value: Double
					try { value = args[2].toDouble() }
					catch(npException: NullPointerException) { sender.sendMessage(warning); return false }
					catch(ioobException: IndexOutOfBoundsException) { sender.sendMessage(warning); return false }
					catch(nfException: NumberFormatException) { sender.sendMessage(warning); return false }
					
					if(value in 0.1..1.0) {
						Settings.deltaVelocity = value
						sender.sendMessage("${ChatColor.GREEN}설정되었습니다.")
					}
					else {
						sender.sendMessage(warning)
					}
				}
				
			}
		}

		return true
	}
}