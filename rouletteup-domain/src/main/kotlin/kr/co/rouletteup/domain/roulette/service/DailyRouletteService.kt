package kr.co.rouletteup.domain.roulette.service

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.repository.DailyRouletteRepository
import org.springframework.stereotype.Service

@Service
class DailyRouletteService(
    private val dailyRouletteRepository: DailyRouletteRepository,
) {

    fun save(dailyRoulette: DailyRoulette) {
        dailyRouletteRepository.save(dailyRoulette)
    }

    fun readByRouletteDate(rouletteDate: LocalDate): DailyRoulette? =
        dailyRouletteRepository.findByRouletteDate(rouletteDate)

    fun existsToday(today: LocalDate): Boolean =
        dailyRouletteRepository.existsByRouletteDate(today)
}
