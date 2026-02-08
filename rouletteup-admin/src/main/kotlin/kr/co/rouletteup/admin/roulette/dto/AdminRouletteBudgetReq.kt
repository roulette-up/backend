package kr.co.rouletteup.admin.roulette.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

class AdminRouletteBudgetReq {

    data class UpdateToday(
        @field:NotNull(message = "변경할 예산은 필수입니다.")
        @field:Min(value = 1, message = "예산은 1 이상이어야 합니다.")
        val newTotalBudget: Long,
    )

    data class UpdateFuture(
        @field:NotNull(message = "설정 날짜는 필수입니다.")
        val targetDate: LocalDate,

        @field:NotNull(message = "변경할 예산은 필수입니다.")
        @field:Min(value = 1, message = "예산은 1 이상이어야 합니다.")
        val newTotalBudget: Long,
    )

}
