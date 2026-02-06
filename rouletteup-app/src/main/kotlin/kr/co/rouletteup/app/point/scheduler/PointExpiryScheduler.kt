package kr.co.rouletteup.app.point.scheduler

import kr.co.rouletteup.app.point.service.PointExpiryService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PointExpiryScheduler(
    private val pointExpiryService: PointExpiryService,
) {

    /**
     * 매일 자정에 포인트 만료 처리 로직 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    fun expirePoints() {
        pointExpiryService.expireTodayPoints()
    }
}
