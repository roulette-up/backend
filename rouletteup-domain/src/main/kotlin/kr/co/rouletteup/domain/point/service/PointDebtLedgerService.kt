package kr.co.rouletteup.domain.point.service

import kr.co.rouletteup.domain.point.entity.PointDebtLedger
import kr.co.rouletteup.domain.point.repository.PointDebtLedgerRepository
import kr.co.rouletteup.domain.point.type.DebtLedgerStatus
import org.springframework.stereotype.Service

@Service
class PointDebtLedgerService(
    private val pointDebtLedgerRepository: PointDebtLedgerRepository,
) {

    fun save(pointDebtLedger: PointDebtLedger) {
        pointDebtLedgerRepository.save(pointDebtLedger)
    }

    fun findByUserIdAndStatusOrderByIdAsc(userId: Long, status: DebtLedgerStatus)
            : List<PointDebtLedger> =
        pointDebtLedgerRepository.findByUserIdAndStatusOrderByIdAsc(userId, status)
}
