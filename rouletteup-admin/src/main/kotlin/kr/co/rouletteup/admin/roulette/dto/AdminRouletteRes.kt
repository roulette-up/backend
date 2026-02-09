package kr.co.rouletteup.admin.roulette.dto

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette

data class AdminRouletteRes(
    val id: Long,
    val rouletteDate: LocalDate,
    val totalBudget: Long,
    val usedBudget: Long,
    val participantCount: Int,
) {

    companion object {
        fun form(dailyRoulette: DailyRoulette): AdminRouletteRes =
            AdminRouletteRes(
                id = dailyRoulette.id!!,
                rouletteDate = dailyRoulette.rouletteDate,
                totalBudget = dailyRoulette.totalBudget,
                usedBudget = dailyRoulette.usedBudget,
                participantCount = dailyRoulette.participantCount,
            )
    }
}