package kr.co.rouletteup.admin.point.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ReclaimPointRecordForAdminUseCaseTest {

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @MockK
    private lateinit var userService: UserService

    @MockK
    private lateinit var dailyRouletteService: DailyRouletteService

    @InjectMockKs
    private lateinit var reclaimPointRecordForAdminUseCase: ReclaimPointRecordForAdminUseCase

    @Nested
    @DisplayName("포인트 회수")
    inner class Reclaim {

        @Test
        fun `성공 - 사용한 포인트가 없으면 부채 증가 없이 포인트 취소 및 usedBudget 감소를 수행한다`() {
            // given
            val pointId = 1L
            val rouletteDate = LocalDate.of(2026, 2, 8)
            val granted = 1000L
            val remaining = 1000L // used=0

            val pointRecord = mockk<PointRecord>(relaxed = true)
            val roulette = mockk<DailyRoulette>(relaxed = true)

            every { pointRecordService.readById(pointId) } returns pointRecord
            every { pointRecord.status } returns PointStatus.AVAILABLE
            every { pointRecord.grantedPoint } returns granted
            every { pointRecord.remainingPoint } returns remaining
            every { pointRecord.rouletteDate } returns rouletteDate

            every { dailyRouletteService.readByRouletteDate(rouletteDate) } returns roulette

            // when
            reclaimPointRecordForAdminUseCase.reclaim(pointId)

            // then
            verify(exactly = 1) { pointRecordService.readById(pointId) }
            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(rouletteDate) }

            // used=0이므로 부채 처리 없음
            verify(exactly = 0) { userService.readById(any()) }

            // 핵심 동작
            verify(exactly = 1) { pointRecord.markAsCanceled() }
            verify(exactly = 1) { roulette.decreaseUsedBudget(granted) }
        }

        @Test
        fun `성공 - 사용한 포인트가 있으면 부채를 증가시키고 포인트 취소 및 usedBudget 감소를 수행한다`() {
            // given
            val pointId = 1L
            val rouletteDate = LocalDate.of(2026, 2, 8)
            val userId = 10L

            val granted = 1000L
            val remaining = 400L // used=600
            val used = granted - remaining // 600

            val pointRecord = mockk<PointRecord>(relaxed = true)
            val roulette = mockk<DailyRoulette>(relaxed = true)
            val user = mockk<User>(relaxed = true)

            every { pointRecordService.readById(pointId) } returns pointRecord
            every { pointRecord.status } returns PointStatus.AVAILABLE
            every { pointRecord.grantedPoint } returns granted
            every { pointRecord.remainingPoint } returns remaining
            every { pointRecord.rouletteDate } returns rouletteDate
            every { pointRecord.userId } returns userId

            every { dailyRouletteService.readByRouletteDate(rouletteDate) } returns roulette
            every { userService.readById(userId) } returns user

            // when
            reclaimPointRecordForAdminUseCase.reclaim(pointId)

            // then
            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 1) { user.increaseDebt(used) }

            verify(exactly = 1) { pointRecord.markAsCanceled() }
            verify(exactly = 1) { roulette.decreaseUsedBudget(granted) }
        }

        @Test
        fun `실패 - 포인트 내역이 존재하지 않으면 NOT_FOUND 예외를 던진다`() {
            // given
            val pointId = 1L
            every { pointRecordService.readById(pointId) } returns null

            // when
            val exception = assertThrows(PointException::class.java) {
                reclaimPointRecordForAdminUseCase.reclaim(pointId)
            }

            // then
            assertEquals(PointErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { pointRecordService.readById(pointId) }
            verify(exactly = 0) { dailyRouletteService.readByRouletteDate(any()) }
            verify(exactly = 0) { userService.readById(any()) }
        }

        @Test
        fun `실패 - 이미 CANCELED 상태면 ALREADY_CANCELED 예외를 던진다`() {
            // given
            val pointId = 1L
            val pointRecord = mockk<PointRecord>(relaxed = true)

            every { pointRecordService.readById(pointId) } returns pointRecord
            every { pointRecord.status } returns PointStatus.CANCELED

            // when
            val exception = assertThrows(PointException::class.java) {
                reclaimPointRecordForAdminUseCase.reclaim(pointId)
            }

            // then
            assertEquals(PointErrorType.ALREADY_CANCELED, exception.errorType)

            verify(exactly = 1) { pointRecordService.readById(pointId) }
            verify(exactly = 0) { dailyRouletteService.readByRouletteDate(any()) }
            verify(exactly = 0) { userService.readById(any()) }
            verify(exactly = 0) { pointRecord.markAsCanceled() }
        }

        @Test
        fun `실패 - 룰렛이 존재하지 않으면 Roulette NOT_FOUND 예외를 던진다`() {
            // given
            val pointId = 1L
            val rouletteDate = LocalDate.of(2026, 2, 8)

            val pointRecord = mockk<PointRecord>(relaxed = true)
            every { pointRecordService.readById(pointId) } returns pointRecord
            every { pointRecord.status } returns PointStatus.AVAILABLE
            every { pointRecord.rouletteDate } returns rouletteDate
            every { pointRecord.grantedPoint } returns 1000L
            every { pointRecord.remainingPoint } returns 1000L
            every { pointRecord.userId } returns 10L

            every { dailyRouletteService.readByRouletteDate(rouletteDate) } returns null

            // when
            val exception = assertThrows(RouletteException::class.java) {
                reclaimPointRecordForAdminUseCase.reclaim(pointId)
            }

            // then
            assertEquals(RouletteErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { pointRecordService.readById(pointId) }
            verify(exactly = 1) { dailyRouletteService.readByRouletteDate(rouletteDate) }
            verify(exactly = 0) { userService.readById(any()) }
            verify(exactly = 0) { pointRecord.markAsCanceled() }
        }

        @Test
        fun `실패 - 사용한 포인트가 있는데 사용자가 존재하지 않으면 User NOT_FOUND 예외를 던진다`() {
            // given
            val pointId = 1L
            val rouletteDate = LocalDate.of(2026, 2, 8)
            val userId = 10L

            val granted = 1000L
            val remaining = 400L // used=600

            val pointRecord = mockk<PointRecord>(relaxed = true)
            val roulette = mockk<DailyRoulette>(relaxed = true)

            every { pointRecordService.readById(pointId) } returns pointRecord
            every { pointRecord.status } returns PointStatus.AVAILABLE
            every { pointRecord.grantedPoint } returns granted
            every { pointRecord.remainingPoint } returns remaining
            every { pointRecord.rouletteDate } returns rouletteDate
            every { pointRecord.userId } returns userId

            every { dailyRouletteService.readByRouletteDate(rouletteDate) } returns roulette
            every { userService.readById(userId) } returns null

            // when
            val exception = assertThrows(UserException::class.java) {
                reclaimPointRecordForAdminUseCase.reclaim(pointId)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 0) { pointRecord.markAsCanceled() }
            verify(exactly = 0) { roulette.decreaseUsedBudget(any()) }
        }
    }
}
