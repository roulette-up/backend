package kr.co.rouletteup.domain.point.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.point.type.DebtLedgerStatus
import kr.co.rouletteup.domain.user.entity.User

@Entity
@Table(name = "point_debt_ledger")
class PointDebtLedger(
    pointRecordId: Long,
    repaidAmount: Long,
    status: DebtLedgerStatus = DebtLedgerStatus.APPLIED,
    user: User,
) : BaseEntity() {

    @Column(name = "repaid_amount", nullable = false)
    var repaidAmount: Long = repaidAmount
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: DebtLedgerStatus = status
        protected set

    @Column(name = "point_record_id", nullable = false)
    var pointRecordId: Long = pointRecordId
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    /**
     * 회수 완료
     */
    fun markReversed() {
        this.status = DebtLedgerStatus.REVERSED
    }

    /**
     * 부채를 상환한 포인트도 회수된 상태
     */
    fun markIgnored() {
        this.status = DebtLedgerStatus.IGNORED
    }

    /**
     * 부채로 쓰인 포인트 환급
     */
    fun decreaseRepaidAmount(amount: Long) {
        this.repaidAmount -= amount
    }

}
