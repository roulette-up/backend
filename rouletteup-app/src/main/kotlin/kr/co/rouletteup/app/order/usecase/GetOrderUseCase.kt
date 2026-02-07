package kr.co.rouletteup.app.order.usecase

import kr.co.rouletteup.app.order.dto.dto.OrderDetail
import kr.co.rouletteup.app.order.dto.dto.OrderSummary
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetOrderUseCase(
    private val orderService: OrderService,
) {

    /**
     * 주문 내역 페이징 조회 메서드
     *
     * @param userId 사용자 ID(PK)
     * @param pageable 페이지 객체
     * @return 페이지 처리된 주문 내역 DTO
     */
    @Transactional(readOnly = true)
    fun getOrders(userId: Long, pageable: Pageable): Page<OrderSummary> =
        orderService.readAllByUserId(userId, pageable)
            .map { order -> OrderSummary.from(order) }

    /**
     * 특정 주문 내역 조회 메서드
     *
     * @param orderId 주문 내역 ID(PK)
     * @return 주문 내역 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getOrderById(orderId: Long): OrderDetail {
        val order = orderService.readById(orderId)
            ?: throw OrderException(OrderErrorType.NOT_FOUND)

        return OrderDetail.from(order)
    }
}
