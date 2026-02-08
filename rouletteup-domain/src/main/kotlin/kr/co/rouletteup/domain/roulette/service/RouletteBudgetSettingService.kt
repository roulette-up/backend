package kr.co.rouletteup.domain.roulette.service

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.RouletteBudgetSetting
import kr.co.rouletteup.domain.roulette.repository.RouletteBudgetSettingRepository
import org.springframework.stereotype.Service

@Service
class RouletteBudgetSettingService(
    private val rouletteBudgetSettingRepository: RouletteBudgetSettingRepository,
) {

    fun save(rouletteBudgetSetting: RouletteBudgetSetting): RouletteBudgetSetting =
        rouletteBudgetSettingRepository.save(rouletteBudgetSetting)


    fun readBySettingDate(settingDate: LocalDate): RouletteBudgetSetting? =
        rouletteBudgetSettingRepository.findBySettingDate(settingDate)

    fun readFutureSettings(settingDate: LocalDate)
            : List<RouletteBudgetSetting> =
        rouletteBudgetSettingRepository.findBySettingDateGreaterThanOrderBySettingDateAsc(settingDate)

}
