package kr.co.rouletteup.internal.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "[총 예산 캐시 삭제 (서버 내부 통신에 사용)]", description = "서버 내부 통신에 사용되는 총 예산 캐시 삭제 API")
interface AdminCacheEvictApi {

    @Operation(summary = "총 예산 캐시 삭제", description = "서버 내부 통신에 사용되는 총 예산 캐시 삭제 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "총 예산 캐시 삭제 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "총 예산 캐시 삭제 성공",
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
        )
    )
    fun evictTotalBudget(
        @PathVariable date: String,
    ): ResponseEntity<*>
}