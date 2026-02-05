package kr.co.rouletteup.app.auth.dto

import kr.co.rouletteup.domain.user.entity.User

data class SignInRes(
    val id: Long,
) {

    companion object {
        fun from(user: User): SignInRes =
            SignInRes(
                id = user.id!!
            )
    }
}
