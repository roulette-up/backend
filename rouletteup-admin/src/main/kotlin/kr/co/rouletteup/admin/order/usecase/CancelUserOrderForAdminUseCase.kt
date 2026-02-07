package kr.co.rouletteup.admin.order.usecase

import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderPointUsageService
import kr.co.rouletteup.domain.order.service.OrderService
import kr.co.rouletteup.domain.order.type.OrderStatus
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CancelUserOrderForAdminUseCase(
    private val orderService: OrderService,
    private val orderPointUsageService: OrderPointUsageService,
    private val pointRecordService: PointRecordService,
    private val productService: ProductService,
) {

    /**
     * 어드민이 사용자 주문 내역을 취소하는 메서드
     * - 주문에 사용된 사용자 포인트 내역을 조회하여 환불 진행
     * - 만료 기한을 +3일 처리 (이미 만료된 경우, 현재 날짜 기준)
     * - 상품 재고 업데이트
     */
    @Transactional
    fun cancelUserOrder(orderId: Long) {
        val order = orderService.readById(orderId)
            ?: throw OrderException(OrderErrorType.NOT_FOUND)

        // 이미 취소 처리된 경우 예외 처리
        if (order.status != OrderStatus.COMPLETED) {
            throw OrderException(OrderErrorType.ALREADY_CANCELLED)
        }

        order.cancelByAdmin()

        val usages = orderPointUsageService.readByOrderId(orderId)
        refundUsedPoints(usages)

        // 상품 재고 되돌리기
        productService.increaseStock(order.product.id!!, order.quantity)
    }

    /**
     * 사용한 포인트 환불 처리 메서드
     *
     * @param usages 사용한 포인트 리스트
     */
    private fun refundUsedPoints(
        usages: List<OrderPointUsage>,
    ) {
        // 사용한 포인트 리스트를 통해 포인트 내역 id 추출
        val usedPointRecordIds = usages
            .mapNotNull { usage -> usage.pointRecord.id }
            .distinct()

        if (usedPointRecordIds.isEmpty()) {
            return
        }

        // 포인트 환불을 위해 추출한 id로 포인트 내역 조회
        val pointRecords = pointRecordService.readAllByIds(usedPointRecordIds)

        // Map<id, PointRecord>
        val pointRecordById: Map<Long, PointRecord> = pointRecords
            .mapNotNull { record ->
                val id = record.id
                if (id != null) id to record else null
            }
            .toMap()

        // 환불 처리
        usages.forEach { usage ->
            val pointRecordId = usage.pointRecord.id

            val pointRecord = pointRecordById[pointRecordId]
                ?: throw PointException(PointErrorType.NOT_FOUND)

            pointRecord.refundByAdmin(usage.usedAmount)
        }
    }
}
