package kr.co.rouletteup.app.roulette.service

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RouletteCommandService(
    private val dailyRouletteService: DailyRouletteService,
) {

    private val log = LoggerFactory.getLogger(RouletteCommandService::class.java)

    /**
     * 룰렛 생성 처리 메서드
     * - 하루에 하나만 존재하기에, 이미 존재하면 생성 x
     */
    @Transactional
    fun createTodayIfNotExists() {
        val today = LocalDate.now()

        if (dailyRouletteService.existsToday(today)) {
            log.info("[Roulette] 이미 존재하여 생성 스킵 - date={}", today)
            return
        }

        try {
            dailyRouletteService.save(
                DailyRoulette(
                    rouletteDate = today
                )
            )
            log.info("[Roulette] 금일 룰렛 생성 완료 - date={}", today)
        } catch (e: DataIntegrityViolationException) {
            log.info("[Roulette] 동시 생성 시도 - date={}", today)
        }
    }
}
