package kr.co.rouletteup.admin.auth.controller

import jakarta.validation.Valid
import kr.co.rouletteup.admin.auth.dto.AdminSignInReq
import kr.co.rouletteup.admin.auth.usecase.SignInForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/auth")
class AdminAuthController(
    private val signInForAdminUseCase: SignInForAdminUseCase,
) {

    @PostMapping("/sign-in")
    fun signIn(
        @RequestBody @Valid request: AdminSignInReq,
    ) : ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                signInForAdminUseCase.signIn(request)
            )
        )
}
