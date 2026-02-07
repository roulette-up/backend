package kr.co.rouletteup.admin.order.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.type.OrderStatus

data class AdminOrderSummary(
    val id: Long,
    val quantity: Int,
    val productPrice: Long,
    val productName: String,
    val status: OrderStatus,
    val userId: Long,
    val productId: Long,
    val deletedAt: LocalDateTime?,
) {

    companion object {
        fun from(order: Order): AdminOrderSummary =
            AdminOrderSummary(
                id = order.id!!,
                quantity = order.quantity,
                productPrice = order.productPrice,
                productName = order.productName,
                status = order.status,
                userId = order.user.id!!,
                productId = order.product.id!!,
                deletedAt = order.deletedAt
            )
    }
}
