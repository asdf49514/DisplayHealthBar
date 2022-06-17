package DisplayHealthBar

import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.HashMap

object DataLists
{
	var playerList = HashMap<String, Player>()  //플레이어 이름, 플레이어 객체
	
	var bossBarList = HashMap<String, BossBar>()  //플레이어 이름, 자신 이외에 대미지를 받은 엔티티의 체력을 표시하는 보스바
	var taskID = HashMap<String, Int>()  //플레이어 이름, 보스바의 BukkitRunnable Task ID
	
	var selfBossBarList = HashMap<String, BossBar>()  //플레이어 이름, 자신의 체력을 표시하는 보스바
	var selfTaskID = HashMap<String, Int>()
}