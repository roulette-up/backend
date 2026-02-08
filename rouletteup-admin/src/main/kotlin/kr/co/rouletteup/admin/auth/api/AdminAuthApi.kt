package kr.co.rouletteup.admin.auth.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.rouletteup.admin.auth.dto.AdminSignInReq
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "[인증 API (ADMIN)]", description = "인증 관련 API")
interface AdminAuthApi {

    @Operation(summary = "어드민 로그인", description = "어드민 로그인 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "어드민로그인 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "어드민로그인 성공",
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
                            name = " 닉네임에 공백을 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "nickname": "닉네임은 필수 입력값입니다."
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "403",
            description = "실패 - 어드민이 아님",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "일반 사용자는 접근하지 못함",
                            value = """
                            {
                                "code": "AD001",
                                "message": "관리자 권한이 필요합니다."
                            }
                            """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "실패 - 존재하지 않음",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "사용자 존재하지 않음",
                            value = """
                            {
                                "code": "U001",
                                "message": "사용자가 존재하지 않습니다."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun signIn(
        @RequestBody @Valid request: AdminSignInReq,
    ): ResponseEntity<*>
}