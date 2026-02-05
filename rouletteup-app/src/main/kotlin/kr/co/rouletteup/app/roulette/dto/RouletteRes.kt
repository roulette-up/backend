package kr.co.rouletteup.app.roulette.dto

import kr.co.rouletteup.domain.roulette.entity.DailyRoulette

data class RouletteRes(
    val remainingBudget: Long,
) {

    companion object {
        fun from(dailyRoulette: DailyRoulette): RouletteRes =
            RouletteRes(
                dailyRoulette.remainingBudget
            )
    }
}