package kr.co.rouletteup.admin.auth.dto

import kr.co.rouletteup.domain.user.entity.User

class AdminSignInRes(
    val id: Long,
) {

    companion object {
        fun from(user: User): AdminSignInRes =
            AdminSignInRes(
                id = user.id!!
            )
    }
}
