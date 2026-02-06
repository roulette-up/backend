package kr.co.rouletteup.app.notification.usecase

import kr.co.rouletteup.app.notification.dto.NotificationRes
import kr.co.rouletteup.domain.notification.service.NotificationService
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetNotificationUseCase(
    private val notificationService: NotificationService,
) {

    /**
     * 커서 기반 알림 페이징 조회 메서드
     * - 읽고자 하는 페이지 개수(limit)에서 + 1하여 다음 페이지 여부 확인
     * - 만약 cursorId가 null이라면, 첫페이지 처리
     * - null이 아니라면, 다음페이지 처리
     */
    @Transactional(readOnly = true)
    fun getNotificationsSliceByCursor(
        userId: Long,
        cursorId: Long?,
        limit: Int,
    ): NotificationRes {
        // 다음 페이지 확인을 위해 limit + 1로 설정
        val pageable = PageRequest.of(0, limit + 1)

        val notifications = if (cursorId == null) {
            notificationService.readFirstPage(userId, pageable)
        } else {
            notificationService.readNextPage(userId, cursorId, pageable)
        }

        return NotificationRes.from(
            notifications = notifications,
            pageSize = limit
        )
    }

}
