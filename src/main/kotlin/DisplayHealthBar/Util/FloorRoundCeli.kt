package DisplayHealthBar.Util

import java.lang.IllegalArgumentException

/* 소수 n번째에서 내림/반올림/올림 */

fun floorFrom(value: Double, pos: Int): Double
{
	if(pos < 1)
		throw IllegalArgumentException("\'pos\' should be at least 1.")

	val powOf10 = Math.pow(10.0, pos-1.0)

	return Math.floor(value*powOf10) / powOf10
}

fun roundFrom(value: Double, pos: Int): Double
{
	if(pos < 1)
		throw IllegalArgumentException("\'pos\' should be at least 1.")

	val powOf10 = Math.pow(10.0, pos-1.0)

	return Math.round(value*powOf10) / powOf10
}

fun ceilFrom(value: Double, pos: Int): Double
{
	if(pos < 1)
		throw IllegalArgumentException("\'pos\' should be at least 1.")

	val powOf10 = Math.pow(10.0, pos-1.0)

	return Math.ceil(value*powOf10) / powOf10
}


@JvmName("floorFrom1")
fun Double.floorFrom(pos: Int): Double
{
	if(pos < 1)
		throw IllegalArgumentException("\'pos\' should be at least 1.")
	
	val powOf10 = Math.pow(10.0, pos-1.0)
	
	return Math.floor(this*powOf10) / powOf10
}

@JvmName("roundFrom1")
fun Double.roundFrom(pos: Int): Double
{
	if(pos < 1)
		throw IllegalArgumentException("\'pos\' should be at least 1.")
	
	val powOf10 = Math.pow(10.0, pos-1.0)
	
	return Math.round(this*powOf10) / powOf10
}

@JvmName("ceilFrom1")
fun Double.ceilFrom(pos: Int): Double
{
	if(pos < 1)
		throw IllegalArgumentException("\'pos\' should be at least 1.")
	
	val powOf10 = Math.pow(10.0, pos-1.0)
	
	return Math.ceil(this*powOf10) / powOf10
}