package kr.co.rouletteup.domain.point.dto

data class UserPointDto(
    val userId: Long,
    val availablePointSum: Long,
    val pointDebt: Long,
)
