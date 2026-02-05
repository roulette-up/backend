package kr.co.rouletteup.app.roulette.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kr.co.rouletteup.app.roulette.usercase.GetRouletteUseCase
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.infrastructure.cache.constant.CacheNames
import kr.co.rouletteup.infrastructure.cache.repository.CacheRepository
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetRouletteUseCaseTest {

    @MockK
    private lateinit var dailyRouletteService: DailyRouletteService

    @MockK
    private lateinit var cacheRepository: CacheRepository

    @InjectMockKs
    private lateinit var getRouletteUseCase: GetRouletteUseCase

    @Nested
    @DisplayName("금일 남은 예산 조회")
    inner class GetTodayRemainingBudget {

        @Test
        fun `캐시에 값이 있으면 DB 조회 없이 반환한다`() {
            // given
            val today = LocalDate.now().toString()
            every {
                cacheRepository.get(
                    CacheNames.REMAINING_BUDGET,
                    today,
                    Long::class
                )
            } returns 5000L

            // when
            val result = getRouletteUseCase.getTodayRemainingBudget()

            // then
            assertEquals(5000L, result.remainingBudget)

            verify(exactly = 1) { cacheRepository.get(CacheNames.REMAINING_BUDGET, today, Long::class) }
            verify(exactly = 0) { dailyRouletteService.readByRouletteDate(any()) }
        }

        @Test
        fun `캐시에 없고 DB에 값이 있으면 DB 값으로 반환한다`() {
            // given
            val today = LocalDate.now()
            val dailyRoulette = mockk<DailyRoulette>()
            every { dailyRoulette.remainingBudget } returns 7000L

            every {
                cacheRepository.get(
                    CacheNames.REMAINING_BUDGET,
                    today.toString(),
                    Long::class
                )
            } returns null

            every {
                dailyRouletteService.readByRouletteDate(today)
            } returns dailyRoulette

            // when
            val result = getRouletteUseCase.getTodayRemainingBudget()

            // then
            assertEquals(7000L, result.remainingBudget)

            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(today) }
        }

        @Test
        fun `캐시와 DB 모두 없으면 NOT_FOUND 예외가 발생한다`() {
            // given
            val today = LocalDate.now()

            every {
                cacheRepository.get(
                    CacheNames.REMAINING_BUDGET,
                    today.toString(),
                    Long::class
                )
            } returns null

            every { dailyRouletteService.readByRouletteDate(today) } returns null

            // when
            val exception = assertThrows<RouletteException> {
                getRouletteUseCase.getTodayRemainingBudget()
            }

            // then
            assertEquals(RouletteErrorType.NOT_FOUND, exception.errorType)
        }
    }
}