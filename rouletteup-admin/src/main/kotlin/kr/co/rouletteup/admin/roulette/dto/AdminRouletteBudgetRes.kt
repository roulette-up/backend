package kr.co.rouletteup.admin.roulette.dto

import java.time.LocalDate
import java.time.LocalDateTime
import kr.co.rouletteup.domain.roulette.entity.RouletteBudgetSetting

data class AdminRouletteBudgetRes(
    val id: Long,
    val settingDate: LocalDate,
    val totalBudget: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {

    companion object {
        fun from(rouletteBudgetSetting: RouletteBudgetSetting): AdminRouletteBudgetRes =
            AdminRouletteBudgetRes(
                id = rouletteBudgetSetting.id!!,
                settingDate = rouletteBudgetSetting.settingDate,
                totalBudget = rouletteBudgetSetting.totalBudget,
                createdAt = rouletteBudgetSetting.createdAt,
                modifiedAt = rouletteBudgetSetting.modifiedAt
            )
    }
}