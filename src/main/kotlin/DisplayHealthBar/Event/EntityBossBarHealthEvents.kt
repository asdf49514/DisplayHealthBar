package DisplayHealthBar.Event

import DisplayHealthBar.BossbarList.bossBarList
import DisplayHealthBar.BossbarList.playerList
import DisplayHealthBar.BossbarList.selfBossBarList
import DisplayHealthBar.BossbarList.selfTaskID
import DisplayHealthBar.BossbarList.taskID
import DisplayHealthBar.Main
import DisplayHealthBar.Settings.Settings
import DisplayHealthBar.Util.roundFrom
import org.bukkit.Bukkit
import org.bukkit.Location
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
import org.bukkit.event.entity.EntityRegainHealthEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.lang.Exception
import java.lang.Math.abs

class EntityBossBarHealthEvents : Listener
{
	@EventHandler
	fun onPlayerJoin(event: PlayerJoinEvent) {
		val player = event.player
		playerList[player.name] = player
		
		bossBarList[player.name] = Bukkit.createBossBar("Health Bar", BarColor.GREEN, BarStyle.SEGMENTED_10)
		bossBarList[player.name]!!.addPlayer(player)
		bossBarList[player.name]!!.isVisible = false
		
		selfBossBarList[player.name] = Bukkit.createBossBar("Health Bar(Self)", BarColor.GREEN, BarStyle.SEGMENTED_10)
		selfBossBarList[player.name]!!.addPlayer(player)
		selfBossBarList[player.name]!!.isVisible = false;
		
		taskID[player.name] = Math.random().toInt()
		selfTaskID[player.name] = Math.random().toInt()
	}
	
	@EventHandler
	fun onPlayerQuit(event: PlayerQuitEvent) {
		val player = event.player
		try {
			bossBarList[player.name]!!.removePlayer(player)
			bossBarList.remove(player.name, bossBarList[player.name]!!)
			taskID.remove(player.name)
			
			selfBossBarList[player.name]!!.removePlayer(player)
			selfBossBarList.remove(player.name, bossBarList[player.name]!!)
			selfTaskID.remove(player.name)
		}
		catch (exception: Exception) {
			println("${player.name}의 bossBar를 찾을 수 없습니다. (onPlayerQuit)")
		}
	}
	
	
	private var isDamageBlocked = false
	@EventHandler
	fun entityRelativeDirction(dmgByEntEvent: EntityDamageByEntityEvent)
	{
		val damager = dmgByEntEvent.damager  //공격자
		val victim = dmgByEntEvent.entity   //피격자
		
		val damagerToEntity = damager.location.clone().subtract(victim.location).toVector()
		val victimLooking = victim.location.direction
		val x1 = damagerToEntity.x
		val z1 = damagerToEntity.z
		val x2 = victimLooking.x
		val z2 = victimLooking.z
		val angle = Math.atan2(x1 * z2 - z1 * x2, x1 * x2 + z1 * z2) * 180 / Math.PI
		
		isDamageBlocked = angle in -90.0..90.0
		
		/*
		if (angle >= -45 && angle < 45) {
			// forward
		}
		else if (angle >= 45 && angle < 135) {
			// Move left
		}
		else if (angle in 135.0..180.0 || angle >= -180 && angle < -135) {
			// Move backward
		}
		else if (angle >= -135 && angle < -45) {
			// Move right
		}*/
	}
	
	
	private fun getNearbyEntitiesInRange(entityLocation: Location) = entityLocation.world!!.getNearbyEntities(entityLocation, Settings.recogRangeVertical, Settings.recogRangeHorizontal, Settings.recogRangeHorizontal) //주변의 엔티티들을 가져온다
	
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
				
				lateinit var vic: LivingEntity
				try { vic = event.entity as LivingEntity }  //이벤트 관련 엔티티를 '살아있는 엔티티'로서 가져옴
				catch (ignored: ClassCastException) { return }  //Casting이 안되는 엔티티를 가져오는 상황
				
				val dmg = (event.finalDamage).roundFrom(2)
				var dmgDisplayed = 0.0
				val playerLocation = player.location
				val nearbyEntities: MutableCollection<Entity> = getNearbyEntitiesInRange(playerLocation) //주변의 엔티티들을 가져온다
				
				val isVicEqualToPlayer = vic == player
				
				bossBar = if(isVicEqualToPlayer)
					selfBossBarList[player.name]!!
				else
					bossBarList[player.name]!!
				
				val displayBossBarTask = object : BukkitRunnable() {  //새로운 Task를 생성
					override fun run() {
						bossBar.isVisible = false
					}
				}
				
				
				if((vic is Player) && vic.isBlocking && isDamageBlocked)  return  //대상이 뒤치기 당할때 안뜨는 문제가 생김  (막을때는 시선이 마주침 or 맞은 방향을 알아보는 것을 이용해보자.  https://www.spigotmc.org/threads/players-facing-direction-relative-to-the-entity.420843/, Attacks coming from within a 180° radius in front of a player will be negated,)
				if(event.cause != CRAMMING && event.cause != ENTITY_SWEEP_ATTACK && event.cause != SUICIDE) {  //해당 방식 이외의 방식으로 데미지를 입었을때
					if(vic in nearbyEntities) {  //데미지를 입은게 nearbyEntities에 있다면
						
						val hpReal = (vic.health - dmg).roundFrom(2)
						var hpDisplayed = (vic.health).roundFrom(2)
						val hpPercentageReal: Double = ((hpReal) / vic.maxHealth).roundFrom(2)
						var hpPercentageDisplayed: Double /*= 0= ((hpDisplayed / vic.maxHealth)*100).toInt()*/
						
						//println("hpReal: ${hpReal}, dmg: ${dmg}, realhpReal: ${hpReal-dmg}") //디버깅 전용
						
						
						val colors = getBossbarColors(hpPercentageReal)
						bossBar.color = colors[0] as BarColor
						val chatColor = colors[1] as String
						
						var bossBarTitle: String
						bossBar.isVisible = true
						
						if(dmg == 0.0 || hpDisplayed <= hpReal) {  //아무런 변화가 없을 때 //todo 체력 회복 이벤트에도 똑같이, (능력사용시 저항 5 -> 4or3으로 변경, 하고나서 이 각주 지우기)
							hpPercentageDisplayed = ((hpDisplayed / vic.maxHealth) * 100).roundFrom(2)
							bossBar.progress = getBossBarProgressValue(hpPercentageDisplayed, "Damaged")
							
							bossBarTitle = "${vic.name} $chatColor [HP: ${hpDisplayed.roundFrom(2)} / ${(vic.maxHealth).roundFrom(2)} (${hpPercentageDisplayed.roundFrom(2)}%)] §c [Damaged: ${dmgDisplayed.roundFrom(2)}]"
							bossBar.setTitle(bossBarTitle)
						}
						else {
							object : BukkitRunnable() {
								override fun run() {
									val delta = getAnimDelta(hpDisplayed, hpReal).roundFrom(2)
									hpDisplayed = (hpDisplayed - delta).roundFrom(2)
									dmgDisplayed = (dmgDisplayed + delta).roundFrom(2)
									
									
									if(hpDisplayed <= 0)  hpDisplayed = 0.0
									
									hpPercentageDisplayed = ((hpDisplayed / vic.maxHealth) * 100).roundFrom(2)
									bossBar.progress = getBossBarProgressValue(hpPercentageDisplayed, "Damaged")
									
									
									bossBarTitle = "${vic.name} $chatColor [HP: ${hpDisplayed.roundFrom(2)} / ${(vic.maxHealth).roundFrom(2)} (${hpPercentageDisplayed.roundFrom(2)}%)] §c [Damaged: ${dmgDisplayed.roundFrom(2)}]"
									bossBar.setTitle(bossBarTitle)
									
									if(hpDisplayed <= hpReal || hpDisplayed <= 0)
										this.cancel()
								}
							}.runTaskTimer(getPlugin(Main::class.java), 1L, 1L)
						}
						
						val task = if(isVicEqualToPlayer) selfTaskID[player.name]!!  else taskID[player.name]!!
						
						Bukkit.getScheduler().cancelTask(task) //이전의 Task를 중지하고
						displayBossBarTask.runTaskLater(getPlugin(Main::class.java), 200L)  //새로 생성된 Task 실행하고
						
						if(isVicEqualToPlayer)
							selfTaskID[player.name] = displayBossBarTask.taskId
						else
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
				
				lateinit var vic: LivingEntity
				try { vic = event.entity as LivingEntity }  //이벤트 관련 엔티티를 '살아있는 엔티티'로서 가져옴
				catch (ignored: ClassCastException) { return }  //Casting이 안되는 엔티티를 가져오는 상황이기 때문에 아래의 코드가 작동하지 않게끔 바로 함수를 종료시킨다.
				
				val amount = event.amount
				var amountDisplayed = 0.0
				val playerLocation = player.location
				val nearbyEntities: MutableCollection<Entity> = getNearbyEntitiesInRange(playerLocation) //주변의 엔티티들을 가져온다
				
				val isVicEqualToPlayer = vic == player
				
				bossBar = if(isVicEqualToPlayer)
					selfBossBarList[player.name]!!
				else
					bossBarList[player.name]!!
				
				val displayBossBarTask = object : BukkitRunnable() {  //새로운 Task를 생성
					override fun run() {
						bossBar.isVisible = false
					}
				}
				
				if(event.regainReason != EntityRegainHealthEvent.RegainReason.WITHER) {  //해당 방식이외의 방식으로 회복을 하였을때
					if(vic in nearbyEntities) {  //회복을 한게 nearbyEntities에 있다면
						val hpReal = (vic.health + amount).roundFrom(2)
						var hpDisplayed = (vic.health).roundFrom(2)  //어째선지 바로 이전 체력을 가져옴
						val hpPercentageReal: Double = ((hpReal) / vic.maxHealth).roundFrom(2)
						var hpPercentageDisplayed: Double /*= 0= ((hpDisplayed / vic.maxHealth)*100).toInt()*/
						
						//println("hpReal: ${hpReal}, amount: ${amount}, realhpReal: ${hpReal+amount}") //디버깅 전용
						
						
						val colors = getBossbarColors(hpPercentageReal)
						bossBar.color = colors[0] as BarColor
						val chatColor = colors[1] as String
						
						var bossBarTitle: String
						bossBar.isVisible = true
						
						if(amount == 0.0 || hpDisplayed >= hpReal) {  //아무런 변화가 없을때
							hpPercentageDisplayed = ((hpDisplayed / vic.maxHealth) * 100).roundFrom(2)
							bossBar.progress = getBossBarProgressValue(hpPercentageDisplayed, "Healed")
							
							bossBarTitle = "${vic.name} $chatColor [HP: ${hpDisplayed.roundFrom(2)} / ${(vic.maxHealth).roundFrom(2)} (${hpPercentageDisplayed.roundFrom(2)}%)] §a [Healed: ${amountDisplayed.roundFrom(2)}]"
							bossBar.setTitle(bossBarTitle)
						}
						else {
							object : BukkitRunnable() {
								override fun run() {
									val delta = getAnimDelta(hpDisplayed, hpReal).roundFrom(2)
									hpDisplayed = (hpDisplayed + delta).roundFrom(2)
									amountDisplayed = (amountDisplayed + delta).roundFrom(2)
									
									
									if(hpDisplayed >= vic.maxHealth)  hpDisplayed = vic.maxHealth
									
									hpPercentageDisplayed = ((hpDisplayed / vic.maxHealth) * 100).roundFrom(2)
									bossBar.progress = getBossBarProgressValue(hpPercentageDisplayed, "Healed")
									
									bossBarTitle = "${vic.name} $chatColor [HP: ${hpDisplayed.roundFrom(2)} / ${(vic.maxHealth).roundFrom(2)} (${hpPercentageDisplayed.roundFrom(2)}%)] §a [Healed: ${amountDisplayed.roundFrom(2)}]"
									bossBar.setTitle(bossBarTitle)
									
									if(hpDisplayed >= hpReal || hpDisplayed >= vic.maxHealth)
										this.cancel()
								}
							}.runTaskTimer(getPlugin(Main::class.java), 1L, 1L)
						}
						
						val task = if(isVicEqualToPlayer) selfTaskID[player.name]!!  else taskID[player.name]!!
						
						Bukkit.getScheduler().cancelTask(task) //이전의 Task를 중지하고
						displayBossBarTask.runTaskLater(getPlugin(Main::class.java), 200L)  //새로 생성된 Task 실행하고
						
						if(isVicEqualToPlayer)
							selfTaskID[player.name] = displayBossBarTask.taskId
						else
							taskID[player.name] = displayBossBarTask.taskId  //새로 생성된 Task의 ID를 넣는다.
						
						//player.sendMessage("" + ChatColor.BOLD + "" + ChatColor.AQUA + "${player.name} dealt $dmg damage to ${vic.name}")  //디버그전용
						//sendActionBar(player, "§l§b${vic.name} was healed by $amount.")  //디버그전용
						//sendActionBar(player, "§l§cHpPercentageReal is now $hpPercentageReal")  //디버그전용
					}
				}
			}
		}
	}
	
	private fun getBossbarColors(hpPercentageReal: Double): List<*>
	{
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
		
		return listOf(bossBarColor, chatColor)
	}
	
	private fun getBossBarProgressValue(hpPercentageDisplayed: Double, type: String): Double
	{
		val bossBarProgressValue = when(type) {
			"Damaged" -> {
				if (hpPercentageDisplayed / 100 < 0.0)  0.0 //체력값이 보스바 최대/최소값인 1.0/0.0을 벗어나지 않도록 체력 값을 보정한다.
				else  hpPercentageDisplayed / 100
			}
			"Healed" -> {
				if (hpPercentageDisplayed / 100 > 1.0)  1.0
				else  hpPercentageDisplayed / 100
			}
			else -> throw IllegalArgumentException("Unknown type $type")
		}
		
		return bossBarProgressValue
	}
	
	
	fun getAnimDelta(v1: Double, v2: Double): Double
	{
		val valueDelta = abs(v1 - v2)
		val velocity = Settings.deltaVelocity
		
		return when {
			valueDelta*velocity < 0.1 -> 0.1
			else                      -> valueDelta*velocity
		}
	}
	
	
	
	/*
	private fun Double.calcResistEffect(entity: LivingEntity): Double  //최종 대미지(Final Damage)를 구하려는 목적이었으나, finalDamage의 존재를 알고 나서는 무쓸모
	{
		println("calcResist executed")
		var resistAmp: Int = -1
		for(effect in entity.activePotionEffects) {
			println("current effect is ${effect.type.name}")
			if(effect.type == PotionEffectType.DAMAGE_RESISTANCE) {
				resistAmp = effect.amplifier
				println("found, resisAmp is $resistAmp")
			}
		}
		entity.lastDamage
		if(resistAmp >= 5)
			resistAmp = 4  //5단계, 대미지 100% 감소
		
		val reductionRatio = (resistAmp+1)*20
		
		println("$this - ($this * ($resistAmp+1) / 100)\n  = $this - ${(this * (resistAmp + 1) / 100)}\n  = ${this - (this * (resistAmp + 1) / 100)}")
		return  this - (this * (resistAmp+1) / 100)
	}
	
	/*
	fun getAnimDelta(v1: Double, v2: Double): Double
	{
		val valueDelta = abs(v1 - v2)
		return when {
			valueDelta <= 1.0 -> 0.1
			valueDelta <= 5.0 -> 0.2
			valueDelta <= 10.0 -> 0.3
			valueDelta <= 20.0 -> 0.4
			valueDelta <= 30.0 -> 0.6
			valueDelta <= 40.0 -> 0.7
			else              -> 0.8
		}
	}*/
	 */
}
