package kr.co.rouletteup.app.roulette.initializer

import kr.co.rouletteup.app.roulette.service.RouletteCommandService
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class RouletteInitializer(
    private val rouletteCommandService: RouletteCommandService,
) {

    /**
     * 서버 실행 시 룰렛 생성 이벤트 처리
     */
    @EventListener(ApplicationReadyEvent::class)
    fun onReady() {
        rouletteCommandService.createTodayIfNotExists()
    }
}
