package kr.co.rouletteup.admin.user.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.user.entity.User

data class AdminUserSummary(
    val id: Long,
    val nickname: String,
    val deletedAt: LocalDateTime?,
) {

    companion object {
        fun from(user: User): AdminUserSummary =
            AdminUserSummary(
                id = user.id!!,
                nickname = user.nickname,
                deletedAt = user.deletedAt
            )
    }
}
