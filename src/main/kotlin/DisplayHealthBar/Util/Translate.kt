package DisplayHealthBar.Util

import org.bukkit.ChatColor

fun translateToKor(string: String): String {
	return when(string) {
		"COAL"         -> "석탄"
		"LAPIS_LAZULI" -> "청금석"
		"IRON_INGOT"   -> "철괴"
		"GOLD_INGOT"   -> "금괴"
		"DIAMOND"      -> "다이아몬드"


		"SPEED"           -> "속도 증가"
		"SLOW"            -> "속도 감소"
		"FAST_DIGGING"    -> "성급함"
		"SLOW_DIGGING"    -> "채굴 피로"
		"INCREASE_DAMAGE" -> "힘"

		"HEAL"         -> "즉시 치유"
		"HARM"         -> "즉시 피해"
		"JUMP"         -> "점프 강화"
		"CONFUSION"    -> "멀미"
		"REGENERATION" -> "재생"

		"DAMAGE_RESISTANCE" -> "저항"
		"FIRE_RESISTANCE"   -> "화염 저항"
		"WATER_BREATHING"   -> "수중 호흡"
		"INVISIBILITY"      -> "투명"
		"BLINDNESS"         -> "실명"

		"NIGHT_VISION" -> "야간 투시"
		"HUNGER"       -> "허기"
		"WEAKNESS"     -> "나약함"
		"POISON"       -> "독"
		"WITHER"       -> "시듦"

		"HEALTH_BOOST" -> "생명력 강화"
		"ABSORPTION"   -> "흡수"
		"SATURATION"   -> "포화"
		"GLOWING"      -> "발광"
		"LEVITATION"   -> "공중 부양"

		"LUCK"           -> "행운"
		"UNLUCK"         -> "불운"
		"SLOW_FALLING"   -> "느린 낙하"
		"CONDUIT_POWER"  -> "전달체의 힘"
		"DOLPHINS_GRACE" -> "돌고래의 우아함"

		"BAD_OMEN"            -> "나쁜 징조"
		"HERO_OF_THE_VILLAGE" -> "마을의 영웅"

		else -> "${ChatColor.DARK_RED}Error : Noting Found"
	}
}