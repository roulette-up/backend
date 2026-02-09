package kr.co.rouletteup.admin.order.dto

import kr.co.rouletteup.domain.order.dto.OrderWithNicknameDto
import kr.co.rouletteup.domain.order.type.OrderStatus

data class AdminOrderSummary(
    val id: Long,
    val quantity: Int,
    val productPrice: Long,
    val productName: String,
    val status: OrderStatus,
    val userId: Long,
    val productId: Long,
    val nickname: String,
) {

    companion object {
        fun from(order: OrderWithNicknameDto): AdminOrderSummary =
            AdminOrderSummary(
                id = order.id,
                quantity = order.quantity,
                productPrice = order.productPrice,
                productName = order.productName,
                status = order.status,
                userId = order.userId,
                productId = order.productId,
                nickname = order.nickname
            )
    }
}
