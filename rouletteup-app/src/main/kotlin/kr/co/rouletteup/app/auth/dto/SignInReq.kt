package kr.co.rouletteup.app.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.type.Role

@Schema(description = "로그인 요청 DTO")
data class SignInReq(

    @field:Schema(
        description = "사용자 닉네임",
        example = "test",
        minLength = 2,
        maxLength = 30,
        required = true
    )
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
