package kr.co.rouletteup.app.roulette.usercase

import java.time.LocalDate
import kr.co.rouletteup.app.roulette.dto.RouletteRes
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.infrastructure.cache.constant.CacheNames
import kr.co.rouletteup.infrastructure.cache.repository.CacheRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRouletteUseCase(
    private val dailyRouletteService: DailyRouletteService,
    private val cacheRepository: CacheRepository,
) {

    /**
     * 금일 룰렛 남은 예산 조회 메서드
     * - 예산이 캐시되어 있다면, 캐시된 값 반환
     * - 캐시에 없다면, DB에서 조회 후 반환
     *
     * @return 남은 예산 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getTodayRemainingBudget(): RouletteRes {
        val today = LocalDate.now()

        val cachedRemainingBudget = cacheRepository.get(
            cacheName = CacheNames.REMAINING_BUDGET,
            key = today.toString(),
            type = Long::class
        )

        if (cachedRemainingBudget != null) {
            return RouletteRes(cachedRemainingBudget)
        }

        val dailyRoulette = dailyRouletteService.readByRouletteDate(today)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        return RouletteRes.from(dailyRoulette)
    }
}
