package kr.co.rouletteup.app.product.dto

import kr.co.rouletteup.domain.product.entity.Product

data class ProductSummary(
    val id: Long,
    val name: String,
    val stockQuantity: Int,
    val price: Long,
) {

    companion object {
        fun from(product: Product): ProductSummary =
            ProductSummary(
                id = product.id!!,
                name = product.name,
                stockQuantity = product.stockQuantity,
                price = product.price,
            )
    }
}
