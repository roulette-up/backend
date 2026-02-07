package kr.co.rouletteup.app.order.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "상품 주문 요청 DTO")
data class OrderReq(
    @field:Schema(
        description = "상품 ID(PK)",
        example = "1",
        required = true
    )
    val productId: Long,

    @field:Schema(
        description = "상품 가격",
        example = "1000",
        required = true
    )
    val price: Long,

    @field:Schema(
        description = "구매할 개수",
        example = "3",
        required = true
    )
    val quantity: Int,
)