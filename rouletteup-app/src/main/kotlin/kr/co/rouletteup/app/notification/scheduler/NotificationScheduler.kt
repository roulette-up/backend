package kr.co.rouletteup.app.notification.scheduler

import kr.co.rouletteup.app.notification.service.ExpiryNotificationService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class NotificationScheduler(
    private val expiryNotificationService: ExpiryNotificationService,
) {

    /**
     * 매일 오전 11시 만료 알림 생성 로직 실행
     */
    @Scheduled(cron = "0 0 11 * * *")
    fun notifyExpiringPoints() {
        expiryNotificationService.generateExpiryNoticesForToday()
    }
}
