package kr.co.rouletteup.app.point.service

import java.time.LocalDate
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointExpiryService(
    private val pointRecordService: PointRecordService,
) {

    /**
     * 포인트 만료 처리 로직 실행 메서드
     */
    @Transactional
    fun expireTodayPoints() {
        val today = LocalDate.now()
        pointRecordService.updateStatusToExpiredByDate(today)
    }
}
