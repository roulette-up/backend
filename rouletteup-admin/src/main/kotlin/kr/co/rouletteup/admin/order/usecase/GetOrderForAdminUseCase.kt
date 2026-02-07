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

    /**
     * 전체 주문 내역 조회 메서드 (soft delete 포함)
     * - userId 존재 o -> 사용자를 통한 조회
     * - userId 존재 x -> 전체 조회
     *
     * @param pageable 페이지 크기
     * @return 주문 내역 페이징 DTO
     */
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

    /**
     * 특정 주문 내역 조회 메서드 (soft delete 포함)
     *
     * @param orderId 주문 내역 ID(PK)
     * @return 주문 내역 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getOrderById(orderId: Long): AdminOrderDetail {
        val order = orderService.readByIdIncludeDeleted(orderId)
            ?: throw OrderException(OrderErrorType.NOT_FOUND)

        return AdminOrderDetail.from(order)
    }
}
