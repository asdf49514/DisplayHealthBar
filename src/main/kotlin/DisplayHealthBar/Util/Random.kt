package DisplayHealthBar.Util

fun randomRealNumInRange(min: Double, max: Double): Double
{
	return (Math.random() * (max - min)) + min
}

fun randomRealNumInRangeWithOffset(min: Double, offsetMin: Double, offsetMax: Double, max: Double): Double
{
	var value: Double

	do { value = randomRealNumInRange(min, max) }
	while(offsetMin < value && value < offsetMax)  //offsetMin < value < offsetMax 인 경우 다시뽑는다

	return value
}

fun randomIntInRange(min: Int, max: Int): Int
{
	return ((Math.random() * (max+0.999999 - min)) + min).toInt()

	/*최댓값에 0.999999를 더하는 이유는 Int형으로 변환시 소수점이 버림되는데
	* 최댓값이 3이라고 가정하면
	* 0..0.999999... -> 0
	* 1..1.999999... -> 1
	* 2..2.999999... -> 2
	* 3 -> 3 이 되면서 최댓값이 나올 확률이 더 작아지게 된다.
	* 그래서 최대치에 0.999999를 더해 3..3.999999 -> 3 으로 만들어 확률을 최대한 똑같이 만들어 주는 것이다.*/
}