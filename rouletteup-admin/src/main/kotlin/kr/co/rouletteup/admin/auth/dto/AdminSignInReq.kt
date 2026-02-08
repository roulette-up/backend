package kr.co.rouletteup.admin.auth.dto

import jakarta.validation.constraints.NotBlank

data class AdminSignInReq(

    @field:NotBlank(message = "닉네임은 필수 입력값입니다.")
    val nickname: String,
)