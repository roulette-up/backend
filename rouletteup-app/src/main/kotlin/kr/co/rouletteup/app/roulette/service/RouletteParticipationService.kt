package kr.co.rouletteup.app.roulette.service

import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointDebtLedger
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.policy.PointPolicy
import kr.co.rouletteup.domain.point.service.PointDebtLedgerService
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RouletteParticipationService(
    private val dailyRouletteService: DailyRouletteService,
    private val pointRecordService: PointRecordService,
    private val userService: UserService,
    private val pointDebtLedgerService: PointDebtLedgerService,
) {

    /**
     * 룰렛 참여 결과를 DB에 반영하고 포인트 기록을 생성하는 메서드
     * - usedBudget 증가 + participantCount 증가
     * - User의 포인트 부채를 확인 (있다면 업데이트하고 남는 금액만 point 생성)
     * - PointRecord 생성 (부채 상환으로 reward가 소진되면 status를 USED로 처리)
     *
     * @param userId 사용자 ID(PK)
     * @param date 룰렛 참여 날짜
     * @param reward 보상 포인트 (실제 적립 포인트)
     */
    @Transactional
    fun participateAndRecordPoint(userId: Long, date: LocalDate, reward: Long): Long {
        // 룰렛 예산 업데이트. 실패하면 잔고 부족 처리
        val updated = dailyRouletteService.increaseUsedBudgetAndParticipant(date, reward)
        if (updated == 0) {
            throw RouletteException(RouletteErrorType.BUDGET_EXHAUSTED)
        }

        val user = userService.readById(userId)
            ?: throw UserException(UserErrorType.NOT_FOUND)

        val repaid = user.repayDebt(reward)
        val credit = reward - repaid

        // 포인트 기록 생성
        try {
            val pointRecord = pointRecordService.save(
                PointRecord(
                    grantedPoint = reward,
                    remainingPoint = credit,
                    status = if (credit == 0L) PointStatus.USED else PointStatus.AVAILABLE,
                    expiresAt = date.plusDays(PointPolicy.EXPIRY_DAYS),
                    userId = userId,
                    rouletteDate = date
                )
            )

            // 부채금 삭감 내역이 있으면 해당 기록 저장
            if (repaid > 0) {
                pointDebtLedgerService.save(
                    PointDebtLedger(
                        pointRecordId = pointRecord.id!!,
                        repaidAmount = repaid,
                        user = user
                    )
                )
            }
        } catch (e: DataIntegrityViolationException) {
            throw PointException(PointErrorType.ALREADY_PARTICIPATED)
        }

        return credit
    }
}
