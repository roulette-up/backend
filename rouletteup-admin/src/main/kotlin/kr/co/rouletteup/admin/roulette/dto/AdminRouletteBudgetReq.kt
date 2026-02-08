package kr.co.rouletteup.admin.roulette.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

class AdminRouletteBudgetReq {

    data class UpdateToday(

        @field:Schema(
            description = "변경할 오늘의 총 예산",
            example = "50000",
            minimum = "1",
            required = true
        )
        @field:NotNull(message = "변경할 예산은 필수입니다.")
        @field:Min(value = 1, message = "예산은 1 이상이어야 합니다.")
        val newTotalBudget: Long,
    )

    data class UpdateFuture(
        @field:Schema(
            description = "예산을 적용할 날짜",
            example = "2026-02-10",
            required = true
        )
        @field:NotNull(message = "설정 날짜는 필수입니다.")
        val targetDate: LocalDate,

        @field:Schema(
            description = "변경할 오늘의 총 예산",
            example = "50000",
            minimum = "1",
            required = true
        )
        @field:NotNull(message = "변경할 예산은 필수입니다.")
        @field:Min(value = 1, message = "예산은 1 이상이어야 합니다.")
        val newTotalBudget: Long,
    )

}
