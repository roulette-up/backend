package kr.co.rouletteup.admin.order.controller

import kr.co.rouletteup.admin.order.api.AdminOrderApi
import kr.co.rouletteup.admin.order.usecase.CancelUserOrderForAdminUseCase
import kr.co.rouletteup.admin.order.usecase.GetOrderForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin")
class AdminOrderController(
    private val getOrderForAdminUseCase: GetOrderForAdminUseCase,
    private val cancelUserOrderForAdminUseCase: CancelUserOrderForAdminUseCase,
) : AdminOrderApi {

    @GetMapping("/users/{userId}/orders")
    override fun getOrdersByUserId(
        @PathVariable userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderForAdminUseCase.getOrdersByUserId(userId, pageable)
            )
        )

    @GetMapping("/products/{productId}/orders")
    override fun getOrdersByProductId(
        @PathVariable productId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderForAdminUseCase.getOrdersByProductId(productId, pageable)
            )
        )

    @GetMapping("/orders/{orderId}")
    override fun getOrderById(
        @PathVariable orderId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderForAdminUseCase.getOrderById(orderId)
            )
        )

    @PatchMapping("/orders/{orderId}/cancel")
    fun cancelUserOrder(
        @PathVariable orderId: Long,
    ): ResponseEntity<*> {
        cancelUserOrderForAdminUseCase.cancelUserOrder(orderId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

}
