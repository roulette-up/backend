package kr.co.rouletteup.admin.user.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.type.Role

data class AdminUserDetail(
    val id: Long,
    val nickname: String,
    val pointDebt: Long,
    val role: Role,
    val createdAt: LocalDateTime,
) {

    companion object {
        fun from(user: User): AdminUserDetail =
            AdminUserDetail(
                id = user.id!!,
                nickname = user.nickname,
                pointDebt = user.pointDebt,
                role = user.role,
                createdAt = user.createdAt,
            )
    }
}