package kr.co.rouletteup.domain.order.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.order.type.OrderStatus
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.user.entity.User
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE order SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "orders")
class Order(
    quantity: Int,
    productPrice: Long,
    productName: String,
    status: OrderStatus,
    user: User,
    product: Product,
) : BaseEntity() {

    @Column(nullable = false)
    var quantity: Int = quantity
        protected set

    @Column(name = "product_price", nullable = false)
    var productPrice: Long = productPrice
        protected set

    @Column(name = "product_name", nullable = false)
    var productName: String = productName
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrderStatus = status
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User = user
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    var product: Product = product
        protected set
}
