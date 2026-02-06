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
     * 금일 룰렛 예산 조회 메서드
     * - 총 예산 및 사용 예산이 캐시에 있다면, 반환
     * - 캐시에 없다면, DB에서 조회 후 반환
     *
     * @return 예산 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getTodayBudget(): RouletteRes {
        val today = LocalDate.now()

        // 총 예산 캐시 조회
        val cachedTotalBudget = cacheRepository.get(
            CacheNames.TOTAL_BUDGET, today.toString(), Long::class
        )

        // 사용 예산 캐시 조회
        val cachedUsedBudget = cacheRepository.get(
            CacheNames.USED_BUDGET, today.toString(), Long::class
        )

        if (cachedTotalBudget != null && cachedUsedBudget != null) {
            return RouletteRes(cachedTotalBudget, cachedUsedBudget)
        }

        val dailyRoulette = dailyRouletteService.readByRouletteDate(today)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        return RouletteRes.from(dailyRoulette)
    }
}
