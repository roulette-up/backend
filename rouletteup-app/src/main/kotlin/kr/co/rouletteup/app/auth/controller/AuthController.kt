package kr.co.rouletteup.app.auth.controller

import jakarta.validation.Valid
import kr.co.rouletteup.app.auth.dto.SignInReq
import kr.co.rouletteup.app.auth.usecase.SignInUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val signInUseCase: SignInUseCase,
) {

    @PostMapping("/sign-in")
    fun signIn(
        @RequestBody @Valid request: SignInReq,
    ) : ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                signInUseCase.signIn(request)
            )
        )
}
