package kr.co.rouletteup.app.auth.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.type.Role

data class SignInReq(
    @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
    @field:Size(min = 2, max = 30, message = "닉네임은 2~30자여야 합니다.")
    val nickname: String,
) {
    fun toEntity(): User =
        User(
            nickname = nickname,
            role = Role.USER
        )
}
