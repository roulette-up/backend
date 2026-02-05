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
    @DisplayName("금일 예산 조회")
    inner class GetTodayBudget {

        @Test
        fun `캐시에 값이 있으면 DB 조회 없이 반환한다`() {
            // given
            val today = LocalDate.now().toString()
            every {
                cacheRepository.get(
                    CacheNames.TOTAL_BUDGET,
                    today,
                    Long::class
                )
            } returns 100_000L

            every {
                cacheRepository.get(
                    CacheNames.USED_BUDGET,
                    today,
                    Long::class
                )
            } returns 70_000L

            // when
            val result = getRouletteUseCase.getTodayBudget()

            // then
            assertEquals(100_000L, result.totalBudget)
            assertEquals(70_000L, result.usedBudget)

            verify(exactly = 1) { cacheRepository.get(CacheNames.TOTAL_BUDGET, today, Long::class) }
            verify(exactly = 1) { cacheRepository.get(CacheNames.USED_BUDGET, today, Long::class) }
            verify(exactly = 0) { dailyRouletteService.readByRouletteDate(any()) }
        }

        @Test
        fun `캐시에 없고 DB에 값이 있으면 DB 값으로 반환한다`() {
            // given
            val today = LocalDate.now()
            val dailyRoulette = mockk<DailyRoulette>()
            every { dailyRoulette.totalBudget } returns 100_000L
            every { dailyRoulette.usedBudget } returns 70_000L

            every {
                cacheRepository.get(
                    CacheNames.TOTAL_BUDGET,
                    today.toString(),
                    Long::class
                )
            } returns null

            every {
                cacheRepository.get(
                    CacheNames.USED_BUDGET,
                    today.toString(),
                    Long::class
                )
            } returns null

            every {
                dailyRouletteService.readByRouletteDate(today)
            } returns dailyRoulette

            // when
            val result = getRouletteUseCase.getTodayBudget()

            // then
            assertEquals(100_000L, result.totalBudget)
            assertEquals(70_000L, result.usedBudget)

            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(today) }
        }

        @Test
        fun `캐시와 DB 모두 없으면 NOT_FOUND 예외가 발생한다`() {
            // given
            val today = LocalDate.now()

            every {
                cacheRepository.get(
                    CacheNames.TOTAL_BUDGET,
                    today.toString(),
                    Long::class
                )
            } returns null

            every {
                cacheRepository.get(
                    CacheNames.USED_BUDGET,
                    today.toString(),
                    Long::class
                )
            } returns null

            every { dailyRouletteService.readByRouletteDate(today) } returns null

            // when
            val exception = assertThrows<RouletteException> {
                getRouletteUseCase.getTodayBudget()
            }

            // then
            assertEquals(RouletteErrorType.NOT_FOUND, exception.errorType)
        }
    }
}