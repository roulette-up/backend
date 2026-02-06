package kr.co.rouletteup.app.roulette.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
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
import kr.co.rouletteup.domain.user.entity.User
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
    fun `예산 업데이트 성공 시 debt 상환 후 남은 credit만큼 포인트 기록 저장 및 유저 포인트 적립이 수행된다`() {
        // given
        val userId = 10L
        val date = LocalDate.of(2026, 2, 6)
        val reward = 500L

        val user = mockk<User>(relaxed = true)

        every { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) } returns 1
        every { userService.readById(userId) } returns user

        // debt 상환: 200 상환 -> credit 300
        every { user.repayDebt(reward) } returns 200L

        val recordSlot = slot<PointRecord>()
        every { pointRecordService.save(capture(recordSlot)) } just Runs

        // when
        rouletteParticipationService.participateAndRecordPoint(userId, date, reward)

        // then
        verify(exactly = 1) { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) }
        verify(exactly = 1) { userService.readById(userId) }

        verify(exactly = 1) { user.repayDebt(reward) }

        verify(exactly = 1) { pointRecordService.save(any()) }

        val saved = recordSlot.captured
        assertEquals(reward, saved.grantedPoint)
        assertEquals(300L, saved.remainingPoint)
        assertEquals(PointStatus.AVAILABLE, saved.status)
        assertEquals(date.plusDays(PointPolicy.EXPIRY_DAYS), saved.expiresAt)
        assertEquals(userId, saved.userId)
        assertEquals(date, saved.rouletteDate)
    }

    @Test
    fun `debt 상환으로 reward가 모두 소진되면 USED 상태의 포인트 기록만 저장되고 포인트 적립은 수행되지 않는다`() {
        // given
        val userId = 10L
        val date = LocalDate.of(2026, 2, 6)
        val reward = 500L

        val user = mockk<User>(relaxed = true)

        every { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) } returns 1
        every { userService.readById(userId) } returns user

        // debt 상환: 500 상환 -> credit 0
        every { user.repayDebt(reward) } returns 500L

        val recordSlot = slot<PointRecord>()
        every { pointRecordService.save(capture(recordSlot)) } just Runs

        // when
        rouletteParticipationService.participateAndRecordPoint(userId, date, reward)

        // then
        verify(exactly = 1) { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) }
        verify(exactly = 1) { userService.readById(userId) }
        verify(exactly = 1) { user.repayDebt(reward) }
        verify(exactly = 1) { pointRecordService.save(any()) }

        val saved = recordSlot.captured
        assertEquals(reward, saved.grantedPoint)
        assertEquals(0L, saved.remainingPoint)
        assertEquals(PointStatus.USED, saved.status)
        assertEquals(date.plusDays(PointPolicy.EXPIRY_DAYS), saved.expiresAt)
        assertEquals(userId, saved.userId)
        assertEquals(date, saved.rouletteDate)

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
    }

    @Test
    fun `이미 참여한 기록이 있을 시 예외를 던진다`() {
        // given
        val userId = 10L
        val date = LocalDate.of(2026, 2, 6)
        val reward = 500L

        val user = mockk<User>(relaxed = true)

        every { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) } returns 1
        every { userService.readById(userId) } returns user
        every { user.repayDebt(reward) } returns 0L

        every { pointRecordService.save(any()) } throws DataIntegrityViolationException("duplicate")

        // when
        val exception = assertThrows(PointException::class.java) {
            rouletteParticipationService.participateAndRecordPoint(userId, date, reward)
        }

        // then
        assertEquals(PointErrorType.ALREADY_PARTICIPATED, exception.errorType)

        verify(exactly = 1) { dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward) }
        verify(exactly = 1) { userService.readById(userId) }
        verify(exactly = 1) { user.repayDebt(reward) }

        verify(exactly = 1) { pointRecordService.save(any()) }
    }
}
