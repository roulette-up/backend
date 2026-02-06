package kr.co.rouletteup.app.roulette.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "[룰렛 API]", description = "룰렛 관련 API")
interface RouletteApi {

    @Operation(summary = "룰렛 참여", description = "룰렛 참여 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "룰렛 참여 성공. reward: 당첨금액, credit: 실제 적립 금액 (부채 상환 경우의 수)",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "부채 상환 없을 경우",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "reward": 300,
                                "credit": 300
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "부채 상환 있을 경우",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "reward": 300,
                                "credit": 100
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
            description = "실패 - 이미 참여했거나 예산이 소진됨",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "이미 참여",
                            value = """
                        {
                            "code": "P001",
                            "message": "금일은 이미 참여한 기록이 있습니다."
                        }
                        """
                        ),
                        ExampleObject(
                            name = "예산 소진",
                            value = """
                        {
                            "code": "R002",
                            "message": "금일 룰렛 예산이 모두 소진되었습니다."
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
        ),
        ApiResponse(
            responseCode = "429",
            description = "실패 - 요청이 많아 락 획득 실패(재시도 필요)",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "락 획득 실패",
                            value = """
                            {
                              "code": "G003",
                              "message": "요청이 많아 처리할 수 없습니다. 잠시 후 다시 시도해주세요."
                            }
                            """
                        )
                    ]
                )
            ]
        )
    )
    fun participate(
        @RequestHeader(value = "X-User-Id") userId: Long,
    ): ResponseEntity<*>

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

    @Operation(summary = "금일 룰렛 참여 확인", description = "금일 룰렛 참여 확인 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "금일 룰렛 참여 확인 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "이미 룰렛 참여 했음",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "participated": true
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "룰렛 참여 안했음",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "participated": false
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun checkTodayParticipation(
        @RequestHeader(value = "X-User-Id") userId: Long,
    ): ResponseEntity<*>
}
