package kr.co.rouletteup.app.order.dto.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.type.OrderStatus

data class OrderDetail(
    val id: Long,
    val quantity: Int,
    val productPrice: Long,
    val productName: String,
    val status: OrderStatus,
    val productId: Long,
    val createdAt: LocalDateTime,
) {

    companion object {
        fun from(order: Order): OrderDetail =
            OrderDetail(
                id = order.id!!,
                quantity = order.quantity,
                productPrice = order.productPrice,
                productName = order.productName,
                status = order.status,
                productId = order.product.id!!,
                createdAt = order.createdAt
            )
    }
}