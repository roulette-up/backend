package kr.co.rouletteup.app.roulette.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.policy.PointPolicy
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.user.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.dao.DataIntegrityViolationException

@ExtendWith(MockKExtension::class)
class RouletteParticipationServiceTest {

    @MockK
    private lateinit var dailyRouletteService: DailyRouletteService

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var rouletteParticipationService: RouletteParticipationService

    @Test
    fun `예산 업데이트 성공 시 포인트 기록 저장 및 유저 포인트 적립이 수행된다`() {
        // given
        val userId = 10L
        val date = LocalDate.of(2026, 2, 6)
        val reward = 500L

        every { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) } returns 1
        every { pointRecordService.save(any()) } just Runs
        every { userService.updatePointWithDebt(userId, reward) } just Runs

        val recordSlot = slot<PointRecord>()

        // when
        rouletteParticipationService.participateAndRecordPoint(userId, date, reward)

        // then
        verify(exactly = 1) { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) }

        verify(exactly = 1) { pointRecordService.save(capture(recordSlot)) }
        val saved = recordSlot.captured
        assertEquals(reward, saved.grantedPoint)
        assertEquals(reward, saved.remainingPoint)
        assertEquals(PointStatus.AVAILABLE, saved.status)
        assertEquals(date.plusDays(PointPolicy.EXPIRY_DAYS), saved.expiresAt)
        assertEquals(userId, saved.userId)
        assertEquals(date, saved.rouletteDate)

        verify(exactly = 1) { userService.updatePointWithDebt(userId, reward) }
    }

    @Test
    fun `예산 부족으로 업데이트 실패 시 예외를 던진다`() {
        // given
        val userId = 10L
        val date = LocalDate.of(2026, 2, 6)
        val reward = 500L

        every { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) } returns 0

        // when
        val exception = assertThrows(RouletteException::class.java) {
            rouletteParticipationService.participateAndRecordPoint(userId, date, reward)
        }

        // then
        assertEquals(RouletteErrorType.BUDGET_EXHAUSTED, exception.errorType)

        verify(exactly = 1) { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) }
        verify(exactly = 0) { pointRecordService.save(any()) }
        verify(exactly = 0) { userService.updatePointWithDebt(any(), any()) }
    }

    @Test
    fun `이미 참여한 기록이 있을 시 예외를 던진다`() {
        // given
        val userId = 10L
        val date = LocalDate.of(2026, 2, 6)
        val reward = 500L

        every { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) } returns 1
        every { pointRecordService.save(any()) } throws DataIntegrityViolationException("duplicate")

        // when
        val exception = assertThrows(PointException::class.java) {
            rouletteParticipationService.participateAndRecordPoint(userId, date, reward)
        }

        // then
        assertEquals(PointErrorType.ALREADY_PARTICIPATED, exception.errorType)

        verify(exactly = 1) { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) }
        verify(exactly = 1) { pointRecordService.save(any()) }
        verify(exactly = 0) { userService.updatePointWithDebt(any(), any()) }
    }
}