package kr.co.rouletteup.app.roulette.service

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.roulette.service.RouletteBudgetSettingService
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RouletteCommandService(
    private val dailyRouletteService: DailyRouletteService,
    private val rouletteBudgetSettingService: RouletteBudgetSettingService,
) {

    private val log = LoggerFactory.getLogger(RouletteCommandService::class.java)

    companion object {
        private const val DEFAULT_BUDGET: Long = 100_000L
    }

    /**
     * 룰렛 생성 처리 메서드
     * - 하루에 하나만 존재하기에, 이미 존재하면 생성 x
     * - 생성하고자 하는 날짜의 정해진 예산을 조회하여 적용
     * - 만약 데이터가 없다면 디폴트 값(100,000p) 적용
     */
    @Transactional
    fun createTodayIfNotExists() {
        val today = LocalDate.now()

        if (dailyRouletteService.existsToday(today)) {
            log.info("[Roulette] 이미 존재하여 생성 스킵 - date={}", today)
            return
        }

        val settingTotalBudget = rouletteBudgetSettingService.readBySettingDate(today)?.totalBudget
            ?: DEFAULT_BUDGET

        try {
            dailyRouletteService.save(
                DailyRoulette(
                    rouletteDate = today,
                    totalBudget = settingTotalBudget
                )
            )
            log.info("[Roulette] 금일 룰렛 생성 완료 - date={}", today)
        } catch (e: DataIntegrityViolationException) {
            log.info("[Roulette] 동시 생성 시도 - date={}", today)
        }
    }
}
