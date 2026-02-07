package kr.co.rouletteup.admin.product.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.product.entity.Product

data class AdminProductSummary(
    val id: Long,
    val name: String,
    val stockQuantity: Int,
    val price: Long,
    val deletedAt: LocalDateTime?,
) {

    companion object {
        fun from(product: Product): AdminProductSummary =
            AdminProductSummary(
                id = product.id!!,
                name = product.name,
                stockQuantity = product.stockQuantity,
                price = product.price,
                deletedAt = product.deletedAt
            )
    }
}
