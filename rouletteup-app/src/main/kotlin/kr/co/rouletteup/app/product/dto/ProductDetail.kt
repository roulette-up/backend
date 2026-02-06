package kr.co.rouletteup.app.product.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.product.entity.Product

data class ProductDetail(
    val id: Long,
    val name: String,
    val stockQuantity: Int,
    val price: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
) {

    companion object {
        fun from(product: Product): ProductDetail =
            ProductDetail(
                id = product.id!!,
                name = product.name,
                stockQuantity = product.stockQuantity,
                price = product.price,
                createdAt = product.createdAt,
                modifiedAt = product.modifiedAt
            )
    }
}
