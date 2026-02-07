package kr.co.rouletteup.admin.point.usecase

import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReclaimPointRecordForAdminUseCase(
    private val pointRecordService: PointRecordService,
    private val userService: UserService,
    private val dailyRouletteService: DailyRouletteService,
) {

    /**
     * 포인트 회수 처리 메서드
     * - 회수할 포인트를 조회하여 상태를 회수 처리로 바꿈
     * - 만약 사용한 포인트가 있다면, 사용자 부채 증가
     * - 룰렛의 사용 내역을 포인트 크기만큼 감소
     *
     * @param pointId 포인트 내역 ID(PK)
     */
    @Transactional
    fun reclaim(pointId: Long) {
        val pointRecord = pointRecordService.readById(pointId)
            ?: throw PointException(PointErrorType.NOT_FOUND)

        // 회수 가능 상태 검증
        if (pointRecord.status == PointStatus.CANCELED) {
            throw PointException(PointErrorType.ALREADY_CANCELED)
        }

        val granted = pointRecord.grantedPoint
        val used = granted - pointRecord.remainingPoint

        val roulette = dailyRouletteService.readByRouletteDate(pointRecord.rouletteDate)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        // 사용 내역이 존재한다면 부채 처리
        if (used > 0L) {
            val user = userService.readById(pointRecord.userId)
                ?: throw UserException(UserErrorType.NOT_FOUND)

            user.increaseDebt(used)
        }

        reclaimPointRecordAndRollbackBudget(pointRecord, roulette, granted)
    }

    /**
     * 포인트 취소 및 룰렛 사용 예산 복구 처리 메서드
     *
     * @param pointRecord 취소할 포인트 데이터
     * @param roulette 사용 예산 복구할 룰렛 데이터
     * @param granted 처리할 포인트 크기
     */
    private fun reclaimPointRecordAndRollbackBudget(
        pointRecord: PointRecord,
        roulette: DailyRoulette,
        granted: Long,
    ) {
        pointRecord.markAsCanceled()
        roulette.decreaseUsedBudget(granted)
    }
}
