package kr.co.rouletteup.app.roulette.usecase

import java.time.LocalDate
import kr.co.rouletteup.app.roulette.dto.RouletteStatusRes
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CheckRouletteParticipationUseCase(
    private val pointRecordService: PointRecordService,
) {

    /**
     * 금일 룰렛 참여 확인 메서드
     *
     * @param userId 사용자 ID(PK)
     * @return 참여 상태 DTO
     */
    @Transactional(readOnly = true)
    fun checkTodayParticipation(userId: Long): RouletteStatusRes {
        val today = LocalDate.now()

        return RouletteStatusRes.of(
            pointRecordService.existsByUserIdAndRouletteDate(userId, today)
        )
    }
}
