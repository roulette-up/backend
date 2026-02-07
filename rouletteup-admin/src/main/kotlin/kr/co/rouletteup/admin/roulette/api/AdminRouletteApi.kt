package kr.co.rouletteup.admin.roulette.api

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

@Tag(name = "[룰렛 API (ADMIN)]", description = "관리자 룰렛 처리 관련 API")
interface AdminRouletteApi {

    @Operation(summary = "룰렛 전체 페이징 조회", description = "룰렛 전체 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "룰렛 전체 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "룰렛 전체 페이징 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 1,
                                        "rouletteDate": "2026-02-07",
                                        "totalBudget": 100000,
                                        "usedBudget": 775,
                                        "participantCount": 1,
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
    fun getRoulettes(
        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>

}