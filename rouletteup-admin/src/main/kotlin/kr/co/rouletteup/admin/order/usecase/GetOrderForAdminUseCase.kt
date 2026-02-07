package kr.co.rouletteup.admin.order.usecase

import kr.co.rouletteup.admin.order.dto.AdminOrderDetail
import kr.co.rouletteup.admin.order.dto.AdminOrderSummary
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetOrderForAdminUseCase(
    private val orderService: OrderService,
) {

    @Transactional(readOnly = true)
    fun getOrders(userId: Long?, pageable: Pageable): Page<AdminOrderSummary> {
        return if (userId != null) {
            orderService.readAllByUserIdIncludeDeleted(userId, pageable)
                .map { order -> AdminOrderSummary.from(order) }
        } else {
            orderService.readAllIncludeDeleted(pageable)
                .map { order -> AdminOrderSummary.from(order) }
        }
    }


    @Transactional(readOnly = true)
    fun getOrderById(orderId: Long): AdminOrderDetail {
        val order = orderService.readByIdIncludeDeleted(orderId)
            ?: throw OrderException(OrderErrorType.NOT_FOUND)

        return AdminOrderDetail.from(order)
    }
}
