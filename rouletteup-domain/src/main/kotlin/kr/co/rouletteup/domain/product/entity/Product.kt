package kr.co.rouletteup.domain.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.co.rouletteup.domain.common.entity.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@SQLRestriction("deleted_at IS NULL")
@SQLDelete(sql = "UPDATE product SET deleted_at = NOW() WHERE id = ?")
@Entity
@Table(name = "product")
class Product(
    name: String,
    stockQuantity: Int,
    price: Long,
) : BaseEntity() {

    @Column(length = 100, nullable = false)
    var name: String = name
        protected set

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int = stockQuantity
        protected set

    @Column(nullable = false)
    var price: Long = price
        protected set
}
