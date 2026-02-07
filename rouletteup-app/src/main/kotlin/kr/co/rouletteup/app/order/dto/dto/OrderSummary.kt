package kr.co.rouletteup.app.order.dto.dto

import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.type.OrderStatus

data class OrderSummary(
    val id: Long,
    val quantity: Int,
    val productPrice: Long,
    val productName: String,
    val status: OrderStatus,
) {

    companion object {
        fun from(order: Order): OrderSummary =
            OrderSummary(
                id = order.id!!,
                quantity = order.quantity,
                productPrice = order.productPrice,
                productName = order.productName,
                status = order.status
            )
    }
}