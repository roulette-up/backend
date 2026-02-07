package kr.co.rouletteup.admin.user.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "[사용자 API (ADMIN)]", description = "관리자 사용자 처리 관련 API")
interface AdminUserApi {

    @Operation(summary = "사용자 페이징 조회", description = "사용자 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "사용자 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "상품 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 3,
                                        "nickname": "닉네임3",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 2,
                                        "nickname": "닉네임2",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 1,
                                        "nickname": "닉네임1",
                                        "deletedAt": "2026-02-07T17:11:55.775141"
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "number": 0,
                                    "totalElements": 3,
                                    "totalPages": 1
                                }
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun getUsers(
        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "특정 사용자 조회", description = "특정 사용자 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "특정 사용자 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "특정 사용자 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "id": 2,
                                "nickname": "a1",
                                "pointDebt": 0,
                                "role": "USER",
                                "createdAt": "2026-02-07T17:09:59.482902",
                                "deletedAt": "2026-02-07T17:11:55.775141"
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        ),
        ApiResponse(
            responseCode = "404",
            description = "실패 - 사용자 존재하지 않음",
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
    fun getUserById(
        @PathVariable userId: Long
    ): ResponseEntity<*>
}
