package DisplayHealthBar.Util

import org.bukkit.Material.*
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.LivingEntity
import java.lang.Math.max
import java.lang.Math.min

fun Double.calcFinalDmg(entity: LivingEntity): Double
{
	if(entity !is HumanEntity)  return this
	
	val inv = entity.inventory
	val helmet = inv.helmet
	val chest = inv.chestplate
	val legg = inv.leggings
	val boots = inv.boots
	
	val armors = arrayOf(helmet, chest, legg, boots)
	
	
	var defenseP = 0.0
	var toughP = 0.0
	
	for(armor in armors) {
		when(armor?.type) {
			LEATHER_HELMET   -> defenseP += 1
			GOLDEN_HELMET    -> defenseP += 2
			CHAINMAIL_HELMET -> defenseP += 2
			IRON_HELMET      -> defenseP += 2
			DIAMOND_HELMET   -> { defenseP += 3; toughP += 2 }
			TURTLE_HELMET    -> defenseP += 2
			
			LEATHER_CHESTPLATE   -> defenseP += 3
			GOLDEN_CHESTPLATE    -> defenseP += 5
			CHAINMAIL_CHESTPLATE -> defenseP += 5
			IRON_CHESTPLATE      -> defenseP += 6
			DIAMOND_CHESTPLATE   -> { defenseP += 8; toughP += 2 }
			
			LEATHER_LEGGINGS   -> defenseP += 2
			GOLDEN_LEGGINGS    -> defenseP += 3
			CHAINMAIL_LEGGINGS -> defenseP += 4
			IRON_LEGGINGS      -> defenseP += 5
			DIAMOND_LEGGINGS   -> { defenseP += 6; toughP += 2 }
			
			LEATHER_BOOTS   -> defenseP += 1
			GOLDEN_BOOTS    -> defenseP += 1
			CHAINMAIL_BOOTS -> defenseP += 1
			IRON_BOOTS      -> defenseP += 2
			DIAMOND_BOOTS   -> { defenseP += 3; toughP += 2 }
			
			else -> { /*does nothing*/ }
		}
	}
	
	var damage = this
	damage *= (1 - min(20.0, max(defenseP / 5, defenseP - damage / (toughP / 4 + 2))) / 25)
	return damage
}
