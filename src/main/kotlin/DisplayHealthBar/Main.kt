package DisplayHealthBar

import DisplayHealthBar.Event.EntityBossBarHealthEvents
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin()
{
	private lateinit var commands: CommandManager
	var jPlugin: JavaPlugin = this
	
	override fun onEnable()
	{
		// CommandManager(this)  //?? 이거 없어도 되지 않음?
		//commands = CommandManager(this)
		getCommand("healthbar")?.setExecutor(CommandManager(this))
		Bukkit.getPluginManager().registerEvents(EntityBossBarHealthEvents(), this)  //이벤트 코드 파일을 지정함
		
		
		for(player in Bukkit.getOnlinePlayers()) {
			BossbarList.playerList[player.name] = player
			BossbarList.bossBarList[player.name] = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)
			BossbarList.bossBarList[player.name]!!.addPlayer(player)
			BossbarList.bossBarList[player.name]!!.isVisible = false
			BossbarList.taskID[player.name] = Math.random().toInt()
		}
		
		println("§6<<체력바 표시 플러그인 by asdf49514>>\n§a플러그인이 성공적으로 활성화 되었습니다.§f")
	}
	
	override fun onDisable()
	{
		println("§6<<체력바 표시 플러그인 by asdf49514>>\n§c플러그인이 비활성화 되었습니다.§f")
	}
}