package DisplayHealthBar.Event

import DisplayHealthBar.Main
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle
import org.bukkit.boss.BossBar
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause.*
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.*
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.lang.ClassCastException
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

class EntityBossBarHealthEvents : Listener {
	var playerList = HashMap<String, Player>()
	var bossBarList = HashMap<String, BossBar>()
	private var taskID = HashMap<String, Int>()
	//lateinit var damager: Player
	
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		val player = event.player
		playerList[player.name] = player
		bossBarList[player.name] = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)
		bossBarList[player.name]!!.addPlayer(player)
		bossBarList[player.name]!!.isVisible = false
		taskID[player.name] = Math.random().toInt()
	}
	
	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		val player = event.player
		try {
			bossBarList[player.name]!!.removePlayer(player)
			bossBarList.remove(player.name, bossBarList[player.name]!!)
			taskID.remove(player.name)
		}
		catch(exception: Exception) {
			println("bossBar를 찾을 수 없습니다.")
		}
	}
	
	/*
	@EventHandler
	fun getDamager(event: EntityDamageByEntityEvent) {
		if(event.damager is Player) {
			damager = event.damager as Player
		}
	}
	*/
	private fun getNearbyEntitiesInRange(entityLocation: Location) = entityLocation.world!!.getNearbyEntities(entityLocation, 7.5, 7.5, 7.5) //주변의 엔티티들을 가져온다
	@EventHandler
	fun onEntityDamaged(event: EntityDamageEvent)  //todo 각주 넣기
	{
		val damagedEntity = event.entity
		val entityLocation = damagedEntity.location
		val nearbyEntitiesAroundDamagedEntity: MutableCollection<Entity> = getNearbyEntitiesInRange(entityLocation) //주변의 엔티티들을 가져온다  //todo 아래쪽에도 넣어두기
		lateinit var player: Player
		lateinit var bossBar: BossBar
		for(entity in nearbyEntitiesAroundDamagedEntity) {
			if(entity is Player) {  //플레이어를 발견하면
				player = entity
				bossBar = bossBarList[player.name]!!
				
				/*
				try { player }
				catch(exception: Exception) { println("player를 찾을 수 없음") ; return }  //아래의 코드가 작동해서는 안되므로 함수를 바로 종료시킨다.
				try { bossBar }
				catch(exception: Exception) { println("bossBar를 찾을 수 없음") ; return }
				*/
				
				val displayBossBarTask = object : BukkitRunnable() {  //새로운 Task를 생성
					override fun run() {
						bossBar.isVisible = false
					}
				}
				
				lateinit var vic: LivingEntity
				try {
					vic = event.entity as LivingEntity
				}  //이벤트 관련 엔티티를 '살아있는 엔티티'로서 가져옴
				catch(ignored: ClassCastException) {
					return
				}  //Casting이 안되는 엔티티를 가져오는 상황이기 때문에 아래의 코드가 작동하지 않게끔 바로 함수를 종료시킨다.
				
				val dmg = event.damage
				var dmgDisplayed = -1
				val playerLocation = player.location
				val nearbyEntities: MutableCollection<Entity> = playerLocation.world!!.getNearbyEntities(playerLocation, 7.5, 5.0, 7.5) //주변의 엔티티들을 가져온다
				
				if(event.cause != CRAMMING && event.cause != ENTITY_SWEEP_ATTACK && event.cause != SUICIDE) {  //해당 방식 이외의 방식으로 데미지를 입었을때
					if(vic in nearbyEntities) {  //데미지를 입은게 nearbyEntities에 있다면
						val hpReal = vic.health + 1 - dmg
						var hpDisplayed = (vic.health + 1).toInt()
						val hpPercentageReal: Double = (hpReal - 1) / vic.maxHealth
						var hpPercentageDisplayed: Int /*= 0= ((hpDisplayed / vic.maxHealth)*100).toInt()*/
						
						var bossBarTitle: String
						val bossBarColor: BarColor
						val chatColor: String
						when((hpPercentageReal * 100).toInt()) {
							in 61..200 -> {
								bossBarColor = BarColor.GREEN
								chatColor = "§a"  //초록색
							}
							in 31..60 -> {
								bossBarColor = BarColor.YELLOW
								chatColor = "§e"  //노란색
							}
							else -> { //in 0..30
								bossBarColor = BarColor.RED
								chatColor = "§c"  //빨간색
							}
						}
						
						bossBar.color = bossBarColor
						bossBar.isVisible = true
						
						object : BukkitRunnable() {
							override fun run() {
								if(hpDisplayed <= hpReal || hpDisplayed <= 1) this.cancel()
								
								hpDisplayed--
								dmgDisplayed++
								hpPercentageDisplayed = ((hpDisplayed / vic.maxHealth) * 100).toInt()
								if(hpPercentageDisplayed.toDouble() / 100 < 0.0)
									bossBar.progress = 0.0
								else
									bossBar.progress = hpPercentageDisplayed.toDouble() / 100
								
								bossBarTitle = "${vic.name} $chatColor [HP : ${(hpDisplayed)} / ${(vic.maxHealth).toInt()} (${(hpPercentageDisplayed)}%)] §c [Damaged : ${dmgDisplayed}]"
								bossBar.setTitle(bossBarTitle)
							}
						}.runTaskTimer(getPlugin(Main::class.java), 1L, 1L)
						
						Bukkit.getScheduler().cancelTask(taskID[player.name]!!) //이전의 Task를 중지하고
						displayBossBarTask.runTaskLater(getPlugin(Main::class.java), 200L)  //새로 생성된 Task 실행하고  (40번 줄 참고)
						taskID[player.name] = displayBossBarTask.taskId  //새로 생성된 Task의 ID를 넣는다.
						
						//player.sendMessage("" + ChatColor.BOLD + "" + ChatColor.AQUA + "${player.name} dealt $dmg damage to ${vic.name}")  //디버그전용
						//sendActionBar(player, "§l§c${vic.name} was dealt by $dmg")  //디버그전용
						//sendActionBar(player, "§l§cHpPercentageReal is now $hpPercentageReal")  //디버그전용
					}
				}
			}
		}
	}
	
	
	@EventHandler
	fun onEntityHealed(event: EntityRegainHealthEvent)
	{
		val damagedEntity = event.entity
		val entityLocation = damagedEntity.location
		val nearbyEntitiesAroundDamagedEntity: MutableCollection<Entity> = getNearbyEntitiesInRange(entityLocation) //주변의 엔티티들을 가져온다
		lateinit var player: Player
		lateinit var bossBar: BossBar
		for(entity in nearbyEntitiesAroundDamagedEntity) {
			if(entity is Player) {  //플레이어를 발견하면
				player = entity
				bossBar = bossBarList[player.name]!!
				
				/*
				try { player }
				catch(exception: Exception) { println("player를 찾을 수 없음") ; return }  //아래의 코드가 작동해서는 안되므로 함수를 바로 종료시킨다.
				try { bossBar }
				catch(exception: Exception) { println("bossBar를 찾을 수 없음") ; return }
				*/
				
				val displayBossBarTask = object : BukkitRunnable() {  //새로운 Task를 생성
					override fun run() {
						bossBar.isVisible = false
					}
				}
				
				lateinit var vic: LivingEntity
				try {
					vic = event.entity as LivingEntity
				}  //이벤트 관련 엔티티를 '살아있는 엔티티'로서 가져옴
				catch(ignored: ClassCastException) {
					return
				}  //Casting이 안되는 엔티티를 가져오는 상황이기 때문에 아래의 코드가 작동하지 않게끔 바로 함수를 종료시킨다.
				
				val amount = event.amount
				var amountDisplayed = -1
				val playerLocation = player.location
				val nearbyEntities: MutableCollection<Entity> = playerLocation.world!!.getNearbyEntities(playerLocation, 10.0, 5.0, 10.0) //주변의 엔티티들을 가져온다
				
				if(event.regainReason != WITHER_SPAWN && event.regainReason != EntityRegainHealthEvent.RegainReason.WITHER) {  //해당 방식이외의 방식으로 회복을 하였을때
					if(vic in nearbyEntities) {  //회복을 한게 nearbyEntities에 있다면
						val hpReal = vic.health - 1 + amount
						var hpDisplayed = (vic.health - 1).toInt()  //어째선지 바로 이전 체력을 가져옴
						val hpPercentageReal: Double = (hpReal + 1) / vic.maxHealth
						var hpPercentageDisplayed: Int /*= 0= ((hpDisplayed / vic.maxHealth)*100).toInt()*/
						
						var bossBarTitle: String
						val bossBarColor: BarColor
						val chatColor: String
						when((hpPercentageReal * 100).toInt()) {
							in 61..200 -> {
								bossBarColor = BarColor.GREEN
								chatColor = "§a"  //초록색
							}
							in 31..60 -> {
								bossBarColor = BarColor.YELLOW
								chatColor = "§e"  //노란색
							}
							else -> { //in 0..30
								bossBarColor = BarColor.RED
								chatColor = "§c"  //빨간색
							}
						}
						
						bossBar.color = bossBarColor
						bossBar.isVisible = true
						
						
						object : BukkitRunnable() {
							override fun run() {
								if(hpDisplayed >= hpReal || hpDisplayed >= vic.maxHealth - 1)  // HP : 21/20버그가 나타나지만 실제로 확인될 확률이 매우 희박하므로 수정은 딱히 안함
									this.cancel()
								
								hpDisplayed++
								amountDisplayed++
								hpPercentageDisplayed = ((hpDisplayed / vic.maxHealth) * 100).toInt()
								if(hpPercentageDisplayed.toDouble() / 100 > 1.0) bossBar.progress = 1.0
								else bossBar.progress = hpPercentageDisplayed.toDouble() / 100
								
								bossBarTitle = "${vic.name} $chatColor [HP : ${(hpDisplayed)} / ${(vic.maxHealth).toInt()} (${(hpPercentageDisplayed)}%)] §a [Healed : ${amountDisplayed}]"
								bossBar.setTitle(bossBarTitle)
							}
						}.runTaskTimer(getPlugin(Main::class.java), 1L, 1L)
						
						Bukkit.getScheduler().cancelTask(taskID[player.name]!!)  //이전의 Task를 중지하고
						displayBossBarTask.runTaskLater(getPlugin(Main::class.java), 200L)  //새로 생성된 Task 실행하고  (40번 줄 참고)
						taskID[player.name] = displayBossBarTask.taskId  //새로 생성된 Task의 ID를 넣는다.
						
						//player.sendMessage("" + ChatColor.BOLD + "" + ChatColor.AQUA + "${player.name} dealt $dmg damage to ${vic.name}")  //디버그전용
						//sendActionBar(player, "§l§b${vic.name} was healed by $amount.")  //디버그전용
						//sendActionBar(player, "§l§cHpPercentageReal is now $hpPercentageReal")  //디버그전용
					}
				}
			}
		}
	}
}