package kr.co.rouletteup.app.notification.service

import java.time.LocalDate
import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.service.NotificationService
import kr.co.rouletteup.domain.notification.type.ExpiryNoticeType
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ExpiryNotificationService(
    private val notificationService: NotificationService,
    private val pointRecordService: PointRecordService,
) {

    /**
     * 만료 예정인 포인트 알림 생성 메서드
     * - 만료 일자 기준 3일, 7일 전 알림을 조회
     * - 해당 알림 리스트를 Batch Insert 진행
     */
    @Transactional
    fun generateExpiryNoticesForToday() {
        val today = LocalDate.now()
        val notices = mutableListOf<Notification>()

        notices += buildExpiryNotices(today, ExpiryNoticeType.D7)
        notices += buildExpiryNotices(today, ExpiryNoticeType.D3)

        notificationService.saveAll(notices)
    }

    /**
     * 만료 일정으로부터 알림 객체 생성 메서드
     *
     * @param baseDate 만료 일자 확인 기준 날짜
     * @param type 만료 3일 전(D3), 7일 전(D7)
     * @return 만료 예정인 알림 객체 리스트
     */
    private fun buildExpiryNotices(baseDate: LocalDate, type: ExpiryNoticeType): List<Notification> {
        val targetExpiresAt = baseDate.plusDays(type.daysBefore)
        val targets = pointRecordService.readExpiringOn(targetExpiresAt)

        return targets.map { pr ->
            Notification(
                userId = pr.userId,
                type = type,
                expiresAt = pr.expiresAt,
                expiringPoint = pr.remainingPoint
            )
        }
    }
}
