package kr.co.rouletteup.app.order.dto

data class OrderReq(
    val productId: Long,
    val price: Long,
    val quantity: Int,
)