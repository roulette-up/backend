package kr.co.rouletteup.admin.auth.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "어드민 로그인 요청 DTO")
data class AdminSignInReq(

    @field:Schema(
        description = "어드민 닉네임",
        example = "test",
        minLength = 2,
        maxLength = 30,
        required = true
    )
    @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
    val nickname: String,
)
