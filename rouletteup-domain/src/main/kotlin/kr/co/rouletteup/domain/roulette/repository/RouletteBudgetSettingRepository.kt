package kr.co.rouletteup.domain.roulette.repository

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.RouletteBudgetSetting
import org.springframework.data.jpa.repository.JpaRepository

interface RouletteBudgetSettingRepository : JpaRepository<RouletteBudgetSetting, Long> {
    fun findBySettingDate(settingDate: LocalDate): RouletteBudgetSetting?
    fun findBySettingDateGreaterThanOrderBySettingDateAsc(settingDate: LocalDate): List<RouletteBudgetSetting>
}
