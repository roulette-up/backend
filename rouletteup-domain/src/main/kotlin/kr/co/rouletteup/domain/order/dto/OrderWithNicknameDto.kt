package kr.co.rouletteup.domain.order.dto

import java.time.LocalDateTime
import kr.co.rouletteup.domain.order.type.OrderStatus

data class OrderWithNicknameDto(
    val id: Long,
    val quantity: Int,
    val productPrice: Long,
    val productName: String,
    val status: OrderStatus,
    val userId: Long,
    val productId: Long,
    val createdAt: LocalDateTime,
    val nickname: String,
)