package kr.co.rouletteup.admin.user.dto

import kr.co.rouletteup.domain.user.entity.User

data class AdminUserSummary(
    val id: Long,
    val nickname: String,
) {

    companion object {
        fun from(user: User): AdminUserSummary =
            AdminUserSummary(
                id = user.id!!,
                nickname = user.nickname,
            )
    }
}
