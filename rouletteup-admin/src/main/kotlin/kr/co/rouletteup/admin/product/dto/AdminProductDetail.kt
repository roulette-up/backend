package kr.co.rouletteup.admin.product.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.product.entity.Product

data class AdminProductDetail(
    val id: Long,
    val name: String,
    val stockQuantity: Int,
    val price: Long,
    val createdAt: LocalDateTime,
) {

    companion object {
        fun from(product: Product): AdminProductDetail =
            AdminProductDetail(
                id = product.id!!,
                name = product.name,
                stockQuantity = product.stockQuantity,
                price = product.price,
                createdAt = product.createdAt,
            )
    }
}
