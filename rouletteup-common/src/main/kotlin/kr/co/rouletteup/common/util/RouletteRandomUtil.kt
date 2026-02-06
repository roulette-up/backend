package kr.co.rouletteup.common.util

object RouletteRandomUtil {

    private const val MIN = 100L
    private const val MAX = 1000L

    fun generateReward(limit: Long): Long {
        val upper = minOf(MAX, limit)
        return (MIN..upper).random()
    }
}
