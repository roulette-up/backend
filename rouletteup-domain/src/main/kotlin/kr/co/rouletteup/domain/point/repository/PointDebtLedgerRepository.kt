package kr.co.rouletteup.domain.point.repository

import kr.co.rouletteup.domain.point.entity.PointDebtLedger
import kr.co.rouletteup.domain.point.type.DebtLedgerStatus
import org.springframework.data.jpa.repository.JpaRepository

interface PointDebtLedgerRepository : JpaRepository<PointDebtLedger, Long> {
    fun findByUserIdAndStatusOrderByIdAsc(userId: Long, status: DebtLedgerStatus): List<PointDebtLedger>
}
