package kr.co.rouletteup.domain.product.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
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

    /**
     * 상품 정보 업데이트 편의 메서드
     */
    fun updateInfo(name: String, price: Long) {
        this.name = name
        this.price = price
    }

    /**
     * 상품 재고 증감 처리 편의 메서드
     */
    fun changeStock(amount: Int) {
        val newStock = this.stockQuantity + amount

        if (newStock < 0) {
            throw ProductException(ProductErrorType.INSUFFICIENT_STOCK)
        }

        this.stockQuantity = newStock
    }
}
