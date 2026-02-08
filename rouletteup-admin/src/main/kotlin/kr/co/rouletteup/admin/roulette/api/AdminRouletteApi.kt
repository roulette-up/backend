package kr.co.rouletteup.admin.roulette.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetReq
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody

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

    @Operation(summary = "오늘 룰렛 정보 조회", description = "오늘 룰렛 정보 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "오늘 룰렛 정보 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "오늘 룰렛 정보 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "id": 1,
                                "rouletteDate": "2026-02-07",
                                "totalBudget": 100000,
                                "usedBudget": 775,
                                "participantCount": 1,
                                "deletedAt": null
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
            description = "실패 - 룰렛 존재하지 않음",
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
                        )
                    ]
                )
            ]
        )
    )
    fun getTodayRoulette(): ResponseEntity<*>

    @Operation(summary = "금일 이후의 설정된 총 예산 조회", description = "금일 이후의 설정된 총 예산 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "금일 이후의 설정된 총 예산 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "금일 이후의 설정된 총 예산 조회 성공",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": [
                                {
                                    "id": 1,
                                    "settingDate": "2026-02-09",
                                    "totalBudget": 123456,
                                    "createdAt": "2026-02-08T11:24:13.799116",
                                    "modifiedAt": "2026-02-08T11:24:13.799116"
                                },
                                {
                                    "id": 2,
                                    "settingDate": "2026-02-10",
                                    "totalBudget": 123456,
                                    "createdAt": "2026-02-08T11:25:49.32556",
                                    "modifiedAt": "2026-02-08T11:25:49.32556"
                                },
                                {
                                    "id": 3,
                                    "settingDate": "2026-02-11",
                                    "totalBudget": 123456,
                                    "createdAt": "2026-02-08T11:26:13.256681",
                                    "modifiedAt": "2026-02-08T11:26:13.256681"
                                }
                            ]
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
        )
    )
    fun getFutureSettingsBudget(): ResponseEntity<*>

    @Operation(summary = "금일 룰렛 총 예산 수정", description = "금일 룰렛 총 예산 수정 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "금일 룰렛 총 예산 수정 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "금일 룰렛 총 예산 수정 성공",
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
            description = "유효성 검증 실패 및 잘못된 값 전송",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "정보들 공백으로 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "newTotalBudget": "변경할 예산은 필수입니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "정보들 제약 조건을 만족하지 않는 경우",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "newTotalBudget": "예산은 1 이상이어야 합니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "설정된 예산보다 작은 값을 전송한 경우",
                            value = """
                        {
                              "code": "R003",
                              "message": "금일 예산은 증가만 가능합니다."
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
            description = "실패 - 룰렛 존재하지 않음",
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
                        )
                    ]
                )
            ]
        )
    )
    fun updateTodayBudget(
        @RequestBody @Valid request: AdminRouletteBudgetReq.UpdateToday,
    ): ResponseEntity<*>

    @Operation(summary = "이후 날짜 룰렛 총 예산 수정", description = "이후 날짜 룰렛 총 예산 수정 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "이후 날짜 룰렛 총 예산 수정 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "이후 날짜 룰렛 총 예산 수정 성공",
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
            description = "유효성 검증 실패 및 잘못된 값 전송",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "정보들 공백으로 전송",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "targetDate": "설정 날짜는 필수입니다.",
                                "newTotalBudget": "변경할 예산은 필수입니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "정보들 제약 조건을 만족하지 않는 경우",
                            value = """
                        {
                            "code": "G002",
                            "message": "유효성 검증에 실패하였습니다.",
                            "errors": {
                                "newTotalBudget": "예산은 1 이상이어야 합니다."
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "금일 이후 날짜가 아닌 경우",
                            value = """
                        {
                              "code": "R004",
                              "message": "이후 날짜만 설정 가능합니다."
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
        )
    )
    fun upsertFutureBudget(
        @RequestBody @Valid request: AdminRouletteBudgetReq.UpdateFuture,
    ): ResponseEntity<*>
}