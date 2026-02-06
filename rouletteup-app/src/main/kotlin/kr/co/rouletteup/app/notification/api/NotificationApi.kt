package kr.co.rouletteup.app.notification.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "[알림 API]", description = "알림 관련 API")
interface NotificationApi {

    @Operation(summary = "알림 리스트 페이징 조회", description = "알림 리스트 페이징 조회 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "알림 리스트 페이징 조회 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "다음 페이지가 있는 경우",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "items": [
                                    {
                                        "id": 25,
                                        "type": "D3",
                                        "expiringPoint": 563,
                                        "expiresAt": "2026-02-09",
                                        "createdAt": "2026-02-06T22:37:11",
                                        "isRead": true
                                    },
                                    {
                                        "id": 24,
                                        "type": "D3",
                                        "expiringPoint": 776,
                                        "expiresAt": "2026-02-09",
                                        "createdAt": "2026-02-06T22:37:11",
                                        "isRead": true
                                    },
                                    {
                                        "id": 23,
                                        "type": "D3",
                                        "expiringPoint": 995,
                                        "expiresAt": "2026-02-09",
                                        "createdAt": "2026-02-06T22:37:11",
                                        "isRead": true
                                    }
                                ],
                                "hasNext": true,
                                "nextCursor": 23
                            }
                        }
                        """
                        ),
                        ExampleObject(
                            name = "다음 페이지가 없는 경우",
                            value = """
                        {
                            "code": 200,
                            "message": "요청이 성공하였습니다.",
                            "data": {
                                "items": [
                                    {
                                        "id": 25,
                                        "type": "D3",
                                        "expiringPoint": 563,
                                        "expiresAt": "2026-02-09",
                                        "createdAt": "2026-02-06T22:37:11",
                                        "isRead": true
                                    },
                                    {
                                        "id": 24,
                                        "type": "D3",
                                        "expiringPoint": 776,
                                        "expiresAt": "2026-02-09",
                                        "createdAt": "2026-02-06T22:37:11",
                                        "isRead": true
                                    },
                                    {
                                        "id": 23,
                                        "type": "D3",
                                        "expiringPoint": 995,
                                        "expiresAt": "2026-02-09",
                                        "createdAt": "2026-02-06T22:37:11",
                                        "isRead": true
                                    }
                                ],
                                "hasNext": false,
                                "nextCursor": 23
                            }
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun getNotifications(
        @RequestHeader("X-User-Id") userId: Long,

        @Parameter(name = "현재 페이지의 마지막 id", example = "23")
        @RequestParam(required = false) cursorId: Long?,

        @Parameter(name = "페이지 크기", example = "10")
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<*>

    @Operation(summary = "알림 읽은 처리", description = "알림 읽은 처리 API")
    @ApiResponses(
        ApiResponse(
            responseCode = "200",
            description = "알림 읽은 처리 성공",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "알림 읽은 처리 성공",
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
            responseCode = "404",
            description = "알림 존재하지 않음",
            content = [
                Content(
                    mediaType = "application/json",
                    examples = [
                        ExampleObject(
                            name = "알림 존재하지 않음",
                            value = """
                        {
                            "code": "N001",
                            "message": "알림이 존재하지 않습니다."
                        }
                        """
                        )
                    ]
                )
            ]
        )
    )
    fun markAsRead(
        @PathVariable notificationId: Long,
    ): ResponseEntity<*>
}