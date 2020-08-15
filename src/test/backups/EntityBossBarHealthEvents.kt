//package kotlinasdf.kotlinasdf.Event
//EntityDamagedByEntityEvent를 사용한 체력 이벤트

import kotlinasdf.kotlinasdf.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.roundToInt

class EntityBossBarHealthEvents : Listener
{
	lateinit var bossBar: BossBar
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent)
	{
		bossBar = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)
		bossBar.addPlayer(event.player)
		bossBar.isVisible = false
	}

	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent)
	{
		bossBar.removePlayer(event.player)
	}

	private var taskID: Int = 0
	@EventHandler
	fun onEntityDamagedByPlayer(event: EntityDamageByEntityEvent)  //todo 체력 수치 감소 애니메이션
	{
		if(bossBar == null)  //bossBar가 존재하지 않다면(null 이라면)
			bossBar = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)  //생성한다.


		val displayBossBarTask = object : BukkitRunnable() {  //새로운 Task를 생성
			override fun run() {
				bossBar.isVisible = false
			}
		}

		if(event.damager is Player) {
			val att: Player = event.damager as Player
			val vic: Mob = event.entity as Mob  //엔티티를 몹으로서 가져옴
			val dmg = event.damage
			var dmgDisplayed = 0

			var attHealthReal = vic.health+1 - dmg
			var attHealthDisplayed = vic.health.toInt()
			var hpPercentageReal: Double = (attHealthReal-dmg) / vic.maxHealth
			var hpPercentageDisplayed = 0/*= ((attHealthDisplayed / vic.maxHealth)*100).toInt()*/
			var bossBarTitle = "[${vic.name}]  [HP : ${(attHealthDisplayed)} / ${(vic.maxHealth).toInt()} (${(hpPercentageDisplayed*100).toInt()}%)]  [Damaged : ${dmg.toInt()}]"
			var bossBarColor = when((hpPercentageReal*100).toInt()) {
				in 61..100 -> BarColor.GREEN
				in 31..60 -> BarColor.YELLOW
				else -> BarColor.RED  //in 0..30
			}

			if(hpPercentageReal < 0) { bossBar.progress = 0.0 }
			else { bossBar.progress = hpPercentageReal}
			bossBar.color = bossBarColor
			bossBar.isVisible = true


			object : BukkitRunnable() {
				override fun run() {
					if(attHealthDisplayed == attHealthReal.toInt())
						this.cancel()

					attHealthDisplayed--
					dmgDisplayed++
					hpPercentageDisplayed = (((attHealthDisplayed) / vic.maxHealth)*100).toInt()
					bossBarTitle = "[${vic.name}]  [HP : ${(attHealthDisplayed)} / ${(vic.maxHealth).toInt()} (${(hpPercentageDisplayed)}%)]  [Damaged : ${dmgDisplayed}]"
					bossBar.setTitle(bossBarTitle)
				}
			}.runTaskTimer(getPlugin(Main::class.java), 1L, 1L)


			Bukkit.getScheduler().cancelTask(taskID)  //이전의 Task를 중지하고
			displayBossBarTask.runTaskLater(getPlugin(Main::class.java), 200L)  //새로 생성된 Task 실행하고  (40번 줄 참고)
			taskID = displayBossBarTask.taskId  //새로 생성된 Task의 ID를 넣는다.

			att.sendMessage("" + ChatColor.BOLD + "" + ChatColor.AQUA + "${att.name} dealt $dmg damage to ${vic.name}")  //att.name dealt dmg damage to vic.
		}
	}


}