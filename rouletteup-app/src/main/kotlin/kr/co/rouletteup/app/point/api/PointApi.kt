package kr.co.rouletteup.app.point.api

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
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "[포인트 API]", description = "포인트 관련 API")
interface PointApi {

    @Operation(summary = "포인트 내역 조회", description = "룰렛을 참여하여 획득한 포인트 내역 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "포인트 내역 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "포인트 내역 조회성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "content": [
                                    {
                                        "id": 2,
                                        "grantedPoint": 355,
                                        "remainingPoint": 355,
                                        "status": "AVAILABLE",
                                        "rouletteDate": "2026-02-06",
                                        "expiresAt": "2026-03-08"
                                    },
                                    {
                                        "id": 1,
                                        "grantedPoint": 473,
                                        "remainingPoint": 473,
                                        "status": "AVAILABLE",
                                        "rouletteDate": "2026-02-05",
                                        "expiresAt": "2026-03-07"
                                    }
                                ],
                                "page": {
                                    "size": 10,
                                    "number": 0,
                                    "totalElements": 2,
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
    fun getMyRecords(
        @RequestHeader(value = "X-User-Id") userId: Long,

        @Parameter(
            description = """
                페이징 파라미터  
                - page: 페이지 번호 (기본 0)  
                - size: 페이지 크기 (기본 10)  
                - sort: 정렬 (기본 id, DESC)
            """
        )
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*>
}
