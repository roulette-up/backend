package kr.co.rouletteup.app.point.dto

import kr.co.rouletteup.domain.point.dto.UserPointDto

data class UserPointRes(
    val point: Long,
) {
    companion object {
        fun from(userPoint: UserPointDto): UserPointRes =
            UserPointRes(
                point = userPoint.availablePointSum - userPoint.pointDebt
            )
    }
}