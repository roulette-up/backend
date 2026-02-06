package kr.co.rouletteup.app.notification.controller

import kr.co.rouletteup.app.notification.api.NotificationApi
import kr.co.rouletteup.app.notification.usecase.GetNotificationUseCase
import kr.co.rouletteup.app.notification.usecase.MarkNotificationAsReadUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/notifications")
class NotificationController(
    private val getNotificationUseCase: GetNotificationUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
) : NotificationApi {

    @GetMapping
    override fun getNotifications(
        @RequestHeader("X-User-Id") userId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "20") limit: Int,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getNotificationUseCase.getNotificationsSliceByCursor(
                    userId = userId,
                    cursorId = cursorId,
                    limit = limit,
                )
            )
        )

    @PatchMapping("/{notificationId}/read-confirm")
    override fun markAsRead(
        @PathVariable notificationId: Long,
    ): ResponseEntity<*> {
        markNotificationAsReadUseCase.markNotificationAsRead(notificationId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}