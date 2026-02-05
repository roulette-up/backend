package kr.co.rouletteup.domain.roulette.repository

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import org.springframework.data.jpa.repository.JpaRepository

interface DailyRouletteRepository : JpaRepository<DailyRoulette, Long> {
    fun existsByRouletteDate(rouletteDate: LocalDate): Boolean
}
