package kr.co.rouletteup.app.roulette.dto

import kr.co.rouletteup.domain.roulette.entity.DailyRoulette

data class RouletteRes(
    val totalBudget: Long,
    val usedBudget: Long,
) {

    companion object {
        fun from(dailyRoulette: DailyRoulette): RouletteRes =
            RouletteRes(
                dailyRoulette.totalBudget,
                dailyRoulette.usedBudget
            )
    }
}
