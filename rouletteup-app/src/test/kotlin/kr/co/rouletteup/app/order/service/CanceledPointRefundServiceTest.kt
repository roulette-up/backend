package kr.co.rouletteup.app.order.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.co.rouletteup.domain.point.entity.PointDebtLedger
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.service.PointDebtLedgerService
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.DebtLedgerStatus
import kr.co.rouletteup.domain.point.type.PointStatus
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
class CanceledPointRefundServiceTest {

    @MockK
    private lateinit var userService: UserService

    @MockK
    private lateinit var pointDebtLedgerService: PointDebtLedgerService

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @InjectMockKs
    private lateinit var canceledPointRefundService: CanceledPointRefundService

    @Nested
    @DisplayName("부채가 생긴 포인트 처리 메서드")
    inner class RefundCanceledPointFlow {

        @Test
        fun `성공 - 부채 상환으로 refundAmount가 모두 소진되면 ledger 조회 없이 종료한다`() {
            // given
            val userId = 1L
            val refundAmount = 500L
            val user = mockk<User>(relaxed = true)

            every { userService.readById(userId) } returns user

            // 500 전부 부채 상환으로 소비
            every { user.repayDebt(refundAmount) } returns 500L

            // when
            canceledPointRefundService.refundCanceledPointFlow(userId, refundAmount)

            // then
            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 1) { user.repayDebt(refundAmount) }
            verify(exactly = 0) { pointDebtLedgerService.findByUserIdAndStatusOrderByIdAsc(any(), any()) }
            verify(exactly = 0) { pointRecordService.readAllByIds(any()) }
        }

        @Test
        fun `성공 - 남은 금액만큼 ledger에서 순차 환급하며 repaidAmount가 0이면 REVERSED 처리한다`() {
            // given
            val userId = 1L
            val refundAmount = 700L
            val user = mockk<User>(relaxed = true)

            every { userService.readById(userId) } returns user
            every { user.repayDebt(refundAmount) } returns 0L // 전액 ledger 환급

            val ledger1 = mockLedger(pointRecordId = 10L, initialRepaidAmount = 300L)
            val ledger2 = mockLedger(pointRecordId = 11L, initialRepaidAmount = 500L)

            every {
                pointDebtLedgerService.findByUserIdAndStatusOrderByIdAsc(userId, DebtLedgerStatus.APPLIED)
            } returns listOf(ledger1, ledger2)

            val record1 = mockPointRecord(id = 10L, status = PointStatus.AVAILABLE)
            val record2 = mockPointRecord(id = 11L, status = PointStatus.AVAILABLE)

            every {
                pointRecordService.readAllByIds(match {
                    it.containsAll(listOf(10L, 11L))
                })
            } returns listOf(record1, record2)

            // when
            canceledPointRefundService.refundCanceledPointFlow(userId, refundAmount)

            // then
            // ledger1: 300 환급 후 left 400, ledger1 reversed
            verify(exactly = 1) { record1.restore(300L) }
            verify(exactly = 1) { ledger1.decreaseRepaidAmount(300L) }
            verify(exactly = 1) { ledger1.markReversed() }

            // ledger2: 남은 400 환급(ledger2 repaid 500 -> 100 남음, reversed는 안됨)
            verify(exactly = 1) { record2.restore(400L) }
            verify(exactly = 1) { ledger2.decreaseRepaidAmount(400L) }
            verify(exactly = 0) { ledger2.markReversed() }
        }

        @Test
        fun `실패 - 사용자가 존재하지 않으면 NOT_FOUND 예외를 던진다`() {
            // given
            val userId = 1L
            every { userService.readById(userId) } returns null

            // when
            val exception = assertThrows(UserException::class.java) {
                canceledPointRefundService.refundCanceledPointFlow(userId, 1000L)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 0) { pointDebtLedgerService.findByUserIdAndStatusOrderByIdAsc(any(), any()) }
            verify(exactly = 0) { pointRecordService.readAllByIds(any()) }
        }
    }

    private fun mockPointRecord(id: Long, status: PointStatus): PointRecord {
        val pr = mockk<PointRecord>(relaxed = true)
        every { pr.id } returns id
        every { pr.status } returns status
        return pr
    }

    /**
     * repaidAmount가 decreaseRepaidAmount에 의해 줄어드는 것처럼 보이게 만드는 mock ledger
     */
    private fun mockLedger(pointRecordId: Long, initialRepaidAmount: Long): PointDebtLedger {
        var repaid = initialRepaidAmount

        val ledger = mockk<PointDebtLedger>(relaxed = true)

        every { ledger.pointRecordId } returns pointRecordId
        every { ledger.repaidAmount } answers { repaid }

        every { ledger.decreaseRepaidAmount(any()) } answers {
            val amount = firstArg<Long>()
            repaid -= amount
        }

        every { ledger.markReversed() } just Runs
        every { ledger.markIgnored() } just Runs

        return ledger
    }

}
