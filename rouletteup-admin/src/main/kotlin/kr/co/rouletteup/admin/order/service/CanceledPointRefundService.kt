package kr.co.rouletteup.admin.order.service

import kr.co.rouletteup.domain.point.entity.PointDebtLedger
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.service.PointDebtLedgerService
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.DebtLedgerStatus
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CanceledPointRefundService(
    private val userService: UserService,
    private val pointDebtLedgerService: PointDebtLedgerService,
    private val pointRecordService: PointRecordService,
) {

    /**
     * 사용된 포인트를 어드민이 회수하여 부채가 생긴 포인트 처리 메서드
     * 1. 사용자 부채를 통해 부채 감소
     * 2. 포인트가 남으면 포인트 부채 상환 이력(point_debt_ledger)를 확인하며 포인트 환급
     *
     * @param userId 사용자 ID(PK)
     * @param refundAmount 부채 처리할 포인트
     */
    @Transactional
    fun refundCanceledPointFlow(
        userId: Long,
        refundAmount: Long,
    ) {
        val user = userService.readById(userId)
            ?: throw UserException(UserErrorType.NOT_FOUND)

        var remaining = refundAmount

        // 사용자 부채 먼저 처리
        val repaid = user.repayDebt(remaining)
        remaining -= repaid

        if (remaining <= 0) {
            return
        }

        // 부채 처리 후 남은 금액이 있으면, 부채 상환 이력을 보고 포인트 되돌리기. (APPLIED) id 오름차순 조회
        val ledgers =
            pointDebtLedgerService.findByUserIdAndStatusOrderByIdAsc(userId, DebtLedgerStatus.APPLIED)

        refundFromLedgers(ledgers, remaining)
    }

    /**
     * 포인트 내역(point_record)를 통해 부채로 차감된 포인트 환급 처리 메서드
     * - 부채 상환에 먼저 사용된 포인트부터 처리
     * - 만약 부채 상환에 사용된 포인트도 회수된 것이라면, 상태 변화(IGNORE)하고 다음 내역 처리
     */
    private fun refundFromLedgers(
        ledgers: List<PointDebtLedger>,
        remaining: Long
    ) {
        var left = remaining

        val ledgerPointRecordIds: List<Long> =
            ledgers
                .map { ledger -> ledger.pointRecordId }
                .distinct()

        // 부채 상환에 사용된 포인트 조회
        val ledgerPointRecords = pointRecordService.readAllByIds(ledgerPointRecordIds)

        val ledgerPointRecordById: Map<Long, PointRecord> =
            ledgerPointRecords
                .mapNotNull { record ->
                    val id = record.id
                    if (id != null) id to record else null
                }
                .toMap()

        // 부채 상환을 먼저 처리한 포인트부터 환급 진행
        for (ledger in ledgers) {
            if (left <= 0) {
                break
            }

            val targetPointRecord = ledgerPointRecordById[ledger.pointRecordId]
                ?: throw PointException(PointErrorType.NOT_FOUND)

            // 부채 상환에 사용된 pointRecord가 CANCELED라면 처리하지 않고 IGNORED
            if (targetPointRecord.status == PointStatus.CANCELED) {
                ledger.markIgnored()
                continue
            }

            val revertAmount = minOf(left, ledger.repaidAmount)

            // 포인트 환급
            targetPointRecord.restore(revertAmount)

            // 부채 상환 포인트 환급량 조정
            ledger.decreaseRepaidAmount(revertAmount)
            left -= revertAmount

            // 부채 상환 이력의 포인트가 모두 환급되면 상태 REVERSED 변경
            if (ledger.repaidAmount == 0L) {
                ledger.markReversed()
            }
        }
    }
}
