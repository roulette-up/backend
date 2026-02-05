package kr.co.rouletteup.app.roulette.scheduler

import kr.co.rouletteup.app.roulette.service.RouletteCommandService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RouletteScheduler(
    private val rouletteCommandService: RouletteCommandService,
) {

    /**
     * 매일 자정 룰렛 생성 로직 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    fun createDailyRoulette() {
        rouletteCommandService.createTodayIfNotExists()
    }
}
