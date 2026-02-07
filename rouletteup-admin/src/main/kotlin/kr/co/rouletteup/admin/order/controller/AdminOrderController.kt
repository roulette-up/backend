package kr.co.rouletteup.admin.order.controller

import kr.co.rouletteup.admin.order.api.AdminOrderApi
import kr.co.rouletteup.admin.order.usecase.GetOrderForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/orders")
class AdminOrderController(
    private val getOrderForAdminUseCase: GetOrderForAdminUseCase,
) : AdminOrderApi {

    @GetMapping
    override fun getOrders(
        @RequestParam(required = false) userId: Long?,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderForAdminUseCase.getOrders(userId, pageable)
            )
        )

    @GetMapping("/{orderId}")
    override fun getOrderById(
        @PathVariable orderId: Long
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getOrderForAdminUseCase.getOrderById(orderId)
            )
        )
}
