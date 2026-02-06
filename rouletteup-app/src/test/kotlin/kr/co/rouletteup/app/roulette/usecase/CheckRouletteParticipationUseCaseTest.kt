package kr.co.rouletteup.app.roulette.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.app.roulette.dto.RouletteStatusRes
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CheckRouletteParticipationUseCaseTest {

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @InjectMockKs
    private lateinit var checkRouletteParticipationUseCase: CheckRouletteParticipationUseCase

    @Nested
    @DisplayName("금일 룰렛 참여 확인")
    inner class CheckTodayParticipation {

        @Test
        fun `참여 기록이 존재하면 participated=true를 반환한다`() {
            // given
            val userId = 1L
            val today = LocalDate.now()

            every { pointRecordService.existsByUserIdAndRouletteDate(userId, today) } returns true

            // when
            val result = checkRouletteParticipationUseCase.checkTodayParticipation(userId)

            // then
            assertEquals(RouletteStatusRes.of(true), result)
            verify(exactly = 1) { pointRecordService.existsByUserIdAndRouletteDate(userId, today) }
        }

        @Test
        fun `참여 기록이 없으면 participated=false를 반환한다`() {
            // given
            val userId = 1L
            val today = LocalDate.now()

            every { pointRecordService.existsByUserIdAndRouletteDate(userId, today) } returns false

            // when
            val result = checkRouletteParticipationUseCase.checkTodayParticipation(userId)

            // then
            assertEquals(RouletteStatusRes.of(false), result)
            verify(exactly = 1) { pointRecordService.existsByUserIdAndRouletteDate(userId, today) }
        }
    }
}
