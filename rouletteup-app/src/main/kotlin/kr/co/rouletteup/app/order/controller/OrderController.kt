package kr.co.rouletteup.app.order.controller

import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.app.order.usecase.PurchaseProductUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val purchaseProductUseCase: PurchaseProductUseCase,
) {

    @PostMapping
    fun purchaseProduct(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @RequestBody request: OrderReq,
    ): ResponseEntity<*> {
        purchaseProductUseCase.purchaseProduct(userId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
