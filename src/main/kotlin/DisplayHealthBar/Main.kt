package DisplayHealthBar

import DisplayHealthBar.Event.EntityBossBarHealthEvents
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin()
{
	var jPlugin: JavaPlugin = this

	override fun onEnable()
	{
		Bukkit.getPluginManager().registerEvents(EntityBossBarHealthEvents(), this)  //이벤트 코드 파일을 지정함
		println("§6<<체력바 표시 플러그인 by asdf49514>>\n§a플러그인이 성공적으로 활성화 되었습니다.§f")
	}

	override fun onDisable()
	{
		println("§6<<체력바 표시 플러그인 by asdf49514>>\n§c플러그인이 비활성화 되었습니다.§f")
	}
}