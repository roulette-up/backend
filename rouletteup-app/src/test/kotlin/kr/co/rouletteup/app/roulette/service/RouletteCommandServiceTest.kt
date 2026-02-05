package kr.co.rouletteup.app.roulette.service

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException

@ExtendWith(MockKExtension::class)
class RouletteCommandServiceTest {

    @MockK
    private lateinit var dailyRouletteService: DailyRouletteService

    @InjectMockKs
    private lateinit var rouletteCommandService: RouletteCommandService

    @Nested
    @DisplayName("룰렛 생성")
    inner class CreateTodayIfNotExists {

        @Test
        fun `이미 존재하면 생성하지 않는다`() {
            // given
            every { dailyRouletteService.existsToday(any()) } returns true

            // when
            rouletteCommandService.createTodayIfNotExists()

            // then
            verify(exactly = 1) { dailyRouletteService.existsToday(any()) }
            verify(exactly = 0) { dailyRouletteService.save(any()) }
        }

        @Test
        fun `존재하지 않으면 생성한다`() {
            // given
            every { dailyRouletteService.existsToday(any()) } returns false
            every { dailyRouletteService.save(any()) } returns mockk(relaxed = true)

            // when
            rouletteCommandService.createTodayIfNotExists()

            // then
            verify(exactly = 1) { dailyRouletteService.existsToday(any()) }
            verify(exactly = 1) {
                dailyRouletteService.save(match { it.rouletteDate == LocalDate.now() })
            }
        }

        @Test
        fun `존재하지 않아 생성 시도했지만 동시성으로 유니크 예외가 나면 예외를 삼키고 종료한다`() {
            // given
            every { dailyRouletteService.existsToday(any()) } returns false
            every { dailyRouletteService.save(any()) } throws DataIntegrityViolationException("duplicate key")

            // when
            assertDoesNotThrow {
                rouletteCommandService.createTodayIfNotExists()
            }

            // then
            verify(exactly = 1) { dailyRouletteService.existsToday(any()) }
            verify(exactly = 1) { dailyRouletteService.save(any()) }
        }
    }
}