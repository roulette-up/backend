package kr.co.rouletteup.admin.roulette.usecase

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.assertEquals
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetReq
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.entity.RouletteBudgetSetting
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.roulette.service.RouletteBudgetSettingService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockKExtension::class)
class UpdateRouletteBudgetForAdminUseCaseTest {

    @MockK
    private lateinit var dailyRouletteService: DailyRouletteService

    @MockK
    private lateinit var rouletteBudgetSettingService: RouletteBudgetSettingService

    @MockK
    private lateinit var publisher: ApplicationEventPublisher

    @InjectMockKs
    private lateinit var updateRouletteBudgetForAdminUseCase: UpdateRouletteBudgetForAdminUseCase

    @Nested
    @DisplayName("금일 예산 수정")
    inner class UpdateTodayBudget {

        @Test
        fun `성공 - 금일 예산을 더 큰 값으로 수정하면 DailyRoulette의 totalBudget이 변경된다`() {
            // given
            val today = LocalDate.now()
            val request = AdminRouletteBudgetReq.UpdateToday(newTotalBudget = 200_000L)

            val dailyRoulette = mockk<DailyRoulette>(relaxed = true)
            every { dailyRoulette.totalBudget } returns 120_000L

            every { dailyRouletteService.readByRouletteDate(today) } returns dailyRoulette
            every { publisher.publishEvent(any<Any>()) } just Runs
            every { publisher.publishEvent(any<ApplicationEvent>()) } just Runs

            // when
            updateRouletteBudgetForAdminUseCase.updateTodayBudget(request)

            // then
            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(today) }
            verify(exactly = 1) { dailyRoulette.updateTotalBudget(200_000L) }
            verify(exactly = 1) { publisher.publishEvent(any<Any>()) }
        }

        @Test
        fun `실패 - 금일 룰렛이 없으면 NOT_FOUND 예외가 발생한다`() {
            // given
            val today = LocalDate.now()
            val request = AdminRouletteBudgetReq.UpdateToday(newTotalBudget = 150_000L)

            every { dailyRouletteService.readByRouletteDate(today) } returns null

            // when
            val exception = assertThrows<RouletteException> {
                updateRouletteBudgetForAdminUseCase.updateTodayBudget(request)
            }

            // then
            assertEquals(RouletteErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(today) }
            verify(exactly = 0) { publisher.publishEvent(any()) }
        }

        @Test
        fun `실패 - 금일 예산은 증가만 가능하므로 같거나 작으면 TODAY_BUDGET_ONLY_INCREASE 예외가 발생한다`() {
            // given
            val today = LocalDate.now()
            val request = AdminRouletteBudgetReq.UpdateToday(newTotalBudget = 100_000L)

            val dailyRoulette = mockk<DailyRoulette>(relaxed = true)
            every { dailyRoulette.totalBudget } returns 120_000L

            every { dailyRouletteService.readByRouletteDate(today) } returns dailyRoulette

            // when
            val exception = assertThrows<RouletteException> {
                updateRouletteBudgetForAdminUseCase.updateTodayBudget(request)
            }

            // then
            assertEquals(RouletteErrorType.TODAY_BUDGET_ONLY_INCREASE, exception.errorType)
            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(today) }
            verify(exactly = 0) { dailyRoulette.updateTotalBudget(any()) }
            verify(exactly = 0) { publisher.publishEvent(any()) }
        }

    }

    @Nested
    @DisplayName("이후 날짜 예산 수정")
    inner class UpsertFutureBudget {

        @Test
        fun `성공 - 설정이 이미 존재하면 조회 후 updateTotalBudget만 수행한다`() {
            // given
            val targetDate = LocalDate.now().plusDays(1)
            val request = AdminRouletteBudgetReq.UpdateFuture(
                targetDate = targetDate,
                newTotalBudget = 140_000L
            )

            val setting = mockk<RouletteBudgetSetting>(relaxed = true)

            every { rouletteBudgetSettingService.readBySettingDate(targetDate) } returns setting

            // when
            updateRouletteBudgetForAdminUseCase.upsertFutureBudget(request)

            // then
            verify(exactly = 1) { rouletteBudgetSettingService.readBySettingDate(targetDate) }
            verify(exactly = 0) { rouletteBudgetSettingService.save(any()) }
            verify(exactly = 1) { setting.updateTotalBudget(140_000L) }
        }

        @Test
        fun `성공 - 설정이 없으면 save로 생성 후 updateTotalBudget을 수행한다`() {
            // given
            val targetDate = LocalDate.now().plusDays(2)
            val request = AdminRouletteBudgetReq.UpdateFuture(
                targetDate = targetDate,
                newTotalBudget = 150_000L
            )

            val saved = mockk<RouletteBudgetSetting>(relaxed = true)

            every { rouletteBudgetSettingService.readBySettingDate(targetDate) } returns null
            every { rouletteBudgetSettingService.save(any()) } returns saved

            // when
            updateRouletteBudgetForAdminUseCase.upsertFutureBudget(request)

            // then
            verify(exactly = 1) { rouletteBudgetSettingService.readBySettingDate(targetDate) }
            verify(exactly = 1) {
                rouletteBudgetSettingService.save(
                    match { it.settingDate == targetDate && it.totalBudget == 150_000L }
                )
            }
            verify(exactly = 1) { saved.updateTotalBudget(150_000L) }
        }

        @Test
        fun `실패 - 오늘 또는 과거 날짜는 FUTURE_DATE_ONLY 예외가 발생한다`() {
            // given
            val today = LocalDate.now()
            val request = AdminRouletteBudgetReq.UpdateFuture(
                targetDate = today,
                newTotalBudget = 130_000L
            )

            // when
            val exception = assertThrows<RouletteException> {
                updateRouletteBudgetForAdminUseCase.upsertFutureBudget(request)
            }

            // then
            assertEquals(RouletteErrorType.FUTURE_DATE_ONLY, exception.errorType)

            verify(exactly = 0) { rouletteBudgetSettingService.readBySettingDate(any()) }
            verify(exactly = 0) { rouletteBudgetSettingService.save(any()) }
            verify(exactly = 0) { dailyRouletteService.readByRouletteDate(any()) }
        }
    }

}