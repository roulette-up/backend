package kr.co.rouletteup.admin.roulette.usecase

import java.time.LocalDate
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetRes
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteRes
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.roulette.service.RouletteBudgetSettingService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRouletteForAdminUseCase(
    private val dailyRouletteService: DailyRouletteService,
    private val rouletteBudgetSettingService: RouletteBudgetSettingService,
) {

    /**
     * 전체 룰렛 조회 메서드 (soft delete 포함)
     *
     * @param pageable 페이지 크기
     * @return 룰렛 정보 페이징 DTO
     */
    @Transactional(readOnly = true)
    fun getRoulettes(pageable: Pageable): Page<AdminRouletteRes> =
        dailyRouletteService.readAllIncludeDeleted(pageable)
            .map { roulette -> AdminRouletteRes.form(roulette) }

    /**
     * 오늘 진행 중인 룰렛 정보 조회 메서드
     *
     * @return 오늘 룰렛 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getTodayRoulette(): AdminRouletteRes {
        val today = LocalDate.now()

        val roulette = dailyRouletteService.readByRouletteDate(today)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        return AdminRouletteRes.form(roulette)
    }

    /**
     * 금일 이후의 설정된 총 예산 조회 메서드
     */
    @Transactional(readOnly = true)
    fun getFutureSettingsBudget(): List<AdminRouletteBudgetRes> =
        rouletteBudgetSettingService.readFutureSettings(LocalDate.now())
            .map { budget -> AdminRouletteBudgetRes.from(budget) }

}
