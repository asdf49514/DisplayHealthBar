package DisplayHealthBar.Util

import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun findNearestLivingEntity(self: Player, radX: Double, radY: Double, radZ: Double): LivingEntity
{
	val nearbyEntities = self.getNearbyEntities(radX, radY, radZ)  //주변의 엔티티들을 가져온다.
	nearbyEntities.remove(self)   //자기 자신은 그 리스트에서 삭제
	for(entity in nearbyEntities) {
		if(entity !is LivingEntity)
			nearbyEntities.remove(entity)  //LivingEntity가 아니면 리스트에서 삭제, 이제 nearbyEntities에는 LivingEntity만 있다.
	}

	lateinit var currNearestEntity: LivingEntity  //효과부여의 대상 (CurrentNearsetEntity, 현재 가장 가까운 엔티티)
	var distance = 100.0  //코드의 작동을 위해 아무 값이나 초기화 해둠
	var isFirst = true
	for(entity in nearbyEntities) {
		if(isFirst) {  //비교를 위한 첫 대상의 초기화
			currNearestEntity = entity as LivingEntity
			distance = self.location.distance(entity.location)
			isFirst = false
		}

		if(distance < self.location.distance(entity.location)) {  //누가 거리가 더 짧은지(가까운지) 검사하고
			currNearestEntity = entity as LivingEntity                 //더 가까우면 대상을 바꿈
			distance = self.location.distance(entity.location)  //더 가까운 대상과의 거리 저장
		}
	}

	return currNearestEntity
}