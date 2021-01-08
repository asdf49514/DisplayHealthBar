package DisplayHealthBar.Util

import DisplayHealthBar.Main
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin.getPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.lang.IllegalArgumentException


/**
 * period must be least 1 (Long)
 * volume must be 0.0f ~ 1.0f
 */
fun playNotes(period: Long, volume: Float, instrument: Sound, score: FloatArray, targetPlayer: Player)
{
	var foundEND = false
	for(note in score) {
		if(note == -2f/*END*/)
			foundEND = true
	}
	if(!foundEND)
		throw IllegalArgumentException("\'END\' is not found.  Please insert \'END\' to the score's end.")

	if(period < 1)
		throw IllegalArgumentException("\'period\' must be least 1(Long).")

	if(volume !in 0.0f..1.0f)
		throw IllegalArgumentException("\'volume\' must be 0.0f ~ 1.0f.")


	var i = 0
	object : BukkitRunnable() {
		override fun run() {
			if(score[i] != -1f/*R*/ && score[i] != -2f)
				targetPlayer.playSound(targetPlayer.location, instrument, volume, score[i])

			else if(score[i] == -2f/*END*/)
				this.cancel()

			i++
		}
	}.runTaskTimer(getPlugin(Main::class.java), 10L, period)
}