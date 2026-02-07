package kr.co.rouletteup.admin.order.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.type.OrderStatus

data class AdminOrderDetail(
    val id: Long,
    val quantity: Int,
    val productPrice: Long,
    val productName: String,
    val status: OrderStatus,
    val userId: Long,
    val productId: Long,
    val createdAt: LocalDateTime,
    val deletedAt: LocalDateTime?,
) {

    companion object {
        fun from(order: Order): AdminOrderDetail =
            AdminOrderDetail(
                id = order.id!!,
                quantity = order.quantity,
                productPrice = order.productPrice,
                productName = order.productName,
                status = order.status,
                userId = order.user.id!!,
                productId = order.product.id!!,
                createdAt = order.createdAt,
                deletedAt = order.deletedAt
            )
    }
}
