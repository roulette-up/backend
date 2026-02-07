package kr.co.rouletteup.domain.order.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.point.entity.PointRecord
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE order_point_usage SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "order_point_usage")
class OrderPointUsage(
    usedAmount: Long,
    order: Order,
    pointRecord: PointRecord,
) : BaseEntity() {

    @Column(name = "used_amount", nullable = false)
    var usedAmount: Long = usedAmount
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    var order: Order = order
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_id", nullable = false)
    var pointRecord: PointRecord = pointRecord
        protected set
}
