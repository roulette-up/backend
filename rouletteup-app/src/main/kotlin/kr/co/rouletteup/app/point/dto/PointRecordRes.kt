package kr.co.rouletteup.app.point.dto

import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.type.PointStatus

data class PointRecordRes(
    val id: Long,
    val grantedPoint: Long,
    val remainingPoint: Long,
    val status: PointStatus,
    val rouletteDate: LocalDate,
    val expiresAt: LocalDate,
) {
    companion object {
        fun from(pointRecord: PointRecord) = PointRecordRes(
            id = pointRecord.id!!,
            grantedPoint = pointRecord.grantedPoint,
            remainingPoint = pointRecord.remainingPoint,
            status = pointRecord.status,
            rouletteDate = pointRecord.rouletteDate,
            expiresAt = pointRecord.expiresAt
        )
    }
}
