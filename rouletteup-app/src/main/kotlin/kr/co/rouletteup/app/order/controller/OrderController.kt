package kr.co.rouletteup.app.order.controller

import kr.co.rouletteup.app.order.api.OrderApi
import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.app.order.usecase.CancelOrderUseCase
import kr.co.rouletteup.app.order.usecase.GetOrderUseCase
import kr.co.rouletteup.app.order.usecase.PurchaseProductUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val purchaseProductUseCase: PurchaseProductUseCase,
    private val getOrderUseCase: GetOrderUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
) : OrderApi {

    @PostMapping
    override fun purchaseProduct(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @RequestBody request: OrderReq,
    ): ResponseEntity<*> {
        purchaseProductUseCase.purchaseProduct(userId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping("/my")
    override fun getOrders(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderUseCase.getOrders(userId, pageable)
            )
        )

    @GetMapping("/{orderId}")
    override fun getOrderById(
        @PathVariable orderId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderUseCase.getOrderById(orderId)
            )
        )

    @PatchMapping("/{orderId}/cancel")
    override fun cancelOrder(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @PathVariable orderId: Long,
    ): ResponseEntity<*> {
        cancelOrderUseCase.cancelOrder(userId, orderId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}
