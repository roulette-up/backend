package kr.co.rouletteup.app.roulette.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "[룰렛 API]", description = "룰렛 관련 API")
interface RouletteApi {

    @Operation(summary = "금일 예산 조회", description = "룰렛 처리를 위해 금일 예산 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "금일 예산 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "금일 예산 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "totalBudget": 100000,
                                "usedBudget": 70000
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun getTodayBudget(): ResponseEntity<*>

}
