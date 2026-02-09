package kr.co.rouletteup.domain.point.dto

import java.time.LocalDate
import kr.co.rouletteup.domain.point.type.PointStatus

data class PointRecordWithNicknameDto(
    val id: Long,
    val grantedPoint: Long,
    val remainingPoint: Long,
    val status: PointStatus,
    val expiresAt: LocalDate,
    val userId: Long,
    val rouletteDate: LocalDate,
    val nickname: String
)