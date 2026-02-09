package kr.co.rouletteup.admin.point.dto

import java.time.LocalDate
import kr.co.rouletteup.domain.point.dto.PointRecordWithNicknameDto
import kr.co.rouletteup.domain.point.type.PointStatus

data class AdminPointRes(
    val id: Long,
    val grantedPoint: Long,
    val remainingPoint: Long,
    val status: PointStatus,
    val expiresAt: LocalDate,
    val userId: Long,
    val rouletteDate: LocalDate,
    val nickname: String,
) {

    companion object {
        fun from(pointRecord: PointRecordWithNicknameDto): AdminPointRes =
            AdminPointRes(
                id = pointRecord.id,
                grantedPoint = pointRecord.grantedPoint,
                remainingPoint = pointRecord.remainingPoint,
                status = pointRecord.status,
                expiresAt = pointRecord.expiresAt,
                userId = pointRecord.userId,
                rouletteDate = pointRecord.rouletteDate,
                nickname = pointRecord.nickname
            )
    }
}
