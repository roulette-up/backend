package kr.co.rouletteup.internal.event

import java.time.LocalDate

data class TodayBudgetChangedEvent(
    val date: LocalDate,
)
