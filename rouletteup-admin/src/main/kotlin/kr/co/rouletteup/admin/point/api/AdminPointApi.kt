package kr.co.rouletteup.admin.point.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import java.time.LocalDate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "[포인트 API (ADMIN)]", description = "관리자 포인트 처리 관련 API")
interface AdminPointApi {

    @Operation(summary = "사용자별 포인트 내역 페이징 조회", description = "사용자별 포인트 내역 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "사용자별 포인트 내역 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "사용자별 포인트 내역 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 3,
                                        "grantedPoint": 120,
                                        "remainingPoint": 140,
                                        "status": "AVAILABLE",
                                        "expiresAt": "2026-02-13",
                                        "userId": 1,
                                        "rouletteDate": "2026-01-03",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 2,
                                        "grantedPoint": 120,
                                        "remainingPoint": 120,
                                        "status": "AVAILABLE",
                                        "expiresAt": "2026-02-10",
                                        "userId": 1,
                                        "rouletteDate": "2026-01-02",
                                        "deletedAt": null
                                    },
                                    {
                                        "id": 1,
                                        "grantedPoint": 130,
                                        "remainingPoint": 130,
                                        "status": "EXPIRED",
                                        "expiresAt": "2026-02-09",
                                        "userId": 1,
                                        "rouletteDate": "2026-01-01",
                                        "deletedAt": null
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
    fun getPointRecordByUserId(
        @PathVariable userId: Long,

        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "룰렛 날짜별 포인트 내역 페이징 조회", description = "룰렛 날짜별 포인트 내역 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "룰렛 날짜별 포인트 내역 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "룰렛 날짜별 포인트 내역 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 4,
                                        "grantedPoint": 775,
                                        "remainingPoint": 775,
                                        "status": "AVAILABLE",
                                        "expiresAt": "2026-03-09",
                                        "userId": 3,
                                        "rouletteDate": "2026-02-07",
                                        "deletedAt": null
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "number": 0,
                                    "totalElements": 1,
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
    fun getPointRecordByRouletteDate(
        @PathVariable rouletteDate: LocalDate,

        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

    @Operation(summary = "포인트 회수", description = "포인트 회수하는 API")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "포인트 회수 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "포인트 회수 성공",
                                value = """
                            {
                                "code": 200,
                                "message": "요청이 성공하였습니다."
                            }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "400",
                description = "실패 - 이미 회수된 포인트",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "이미 회수된 포인트",
                                value = """
                            {
                                "code": "P003",
                                "message": "이미 회수된 포인트입니다."
                            }
                            """
                            )
                        ]
                    )
                ]
            ),
            ApiResponse(
                responseCode = "404",
                description = "존재하지 않는 경우",
                content = [
                    Content(
                        mediaType = "application/json",
                        examples = [
                            ExampleObject(
                                name = "룰렛 존재하지 않음",
                                value = """
                            {
                                "code": "R001",
                                "message": "룰렛이 존재하지 않습니다."
                            }
                            """
                            ),
                            ExampleObject(
                                name = "포인트 내역 존재하지 않음",
                                value = """
                            {
                                "code": "P002",
                                "message": "포인트 기록이 존재하지 않습니다."
                            }
                            """
                            ),
                            ExampleObject(
                                name = "(부채 처리 과정) 사용자 존재하지 않음",
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
        ]
    )
    fun reclaimPointRecord(
        @PathVariable pointId: Long,
    ): ResponseEntity<*>
}