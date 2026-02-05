package kr.co.rouletteup.app.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.rouletteup.app.auth.dto.SignInReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "[인증 API]", description = "인증 관련 API")
interface AuthApi {

    @Operation(summary = "로그인", description = "서비스를 사용하기 위한 로그인 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "로그인 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "로그인 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "id": 1
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "400",
            description = "유효성 검증 실패",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "유효성 검증 실패 - 닉네임에 공백을 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "nickname": "닉네임은 필수 입력값입니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "유효성 검증 실패 - 2자 미만 || 30자 초과한 경우",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "nickname": "닉네임은 2~30자여야 합니다."
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun signIn(
        @RequestBody @Valid request: SignInReq,
    ) : ResponseEntity<*>
}
