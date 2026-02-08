package kr.co.rouletteup.admin.roulette.usecase

import java.time.LocalDate
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetReq
import kr.co.rouletteup.domain.roulette.entity.RouletteBudgetSetting
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.roulette.service.RouletteBudgetSettingService
import kr.co.rouletteup.internal.event.TodayBudgetChangedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateRouletteBudgetForAdminUseCase(
    private val dailyRouletteService: DailyRouletteService,
    private val rouletteBudgetSettingService: RouletteBudgetSettingService,
    private val publisher: ApplicationEventPublisher,
) {

    /**
     * 금일 예산 수정 메서드
     * - 증가만 허용
     * - 변경 시 저장된 총 예산 캐시 삭제
     *
     * @param request 금일 예산 수정 DTO
     */
    @Transactional
    fun updateTodayBudget(request: AdminRouletteBudgetReq.UpdateToday) {
        val today = LocalDate.now()

        val dailyRoulette = dailyRouletteService.readByRouletteDate(today)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        // 증가만 가능
        if (request.newTotalBudget <= dailyRoulette.totalBudget) {
            throw RouletteException(RouletteErrorType.TODAY_BUDGET_ONLY_INCREASE)
        }

        dailyRoulette.updateTotalBudget(request.newTotalBudget)

        // 커밋 이후 app 서버 캐시 무효화 트리거
        publisher.publishEvent(TodayBudgetChangedEvent(today))
    }

    /**
     * 이후 날짜 예산 수정 메서드
     * - 증가/감소 모두 가능
     * - roulette_budget_setting에서 조회 후 수정
     * - 없으면 생성
     *
     * @param request 금일 예산 수정 DTO
     */
    @Transactional
    fun upsertFutureBudget(request: AdminRouletteBudgetReq.UpdateFuture) {
        val targetDate = request.targetDate

        // 현재 기준 이후 날짜만 수정 가능
        if (!targetDate.isAfter(LocalDate.now())) {
            throw RouletteException(RouletteErrorType.FUTURE_DATE_ONLY)
        }

        // 해당 날짜 조회. 만약 없다면 요청 값으로 생성
        val setting = rouletteBudgetSettingService.readBySettingDate(targetDate)
            ?: rouletteBudgetSettingService.save(
                RouletteBudgetSetting(
                    settingDate = targetDate,
                    totalBudget = request.newTotalBudget
                )
            )

        setting.updateTotalBudget(request.newTotalBudget)
    }

}