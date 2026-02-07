package kr.co.rouletteup.app.order.usecase

import kr.co.rouletteup.app.order.service.CanceledPointRefundService
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
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CancelOrderUseCase(
    private val orderService: OrderService,
    private val orderPointUsageService: OrderPointUsageService,
    private val pointRecordService: PointRecordService,
    private val productService: ProductService,
    private val canceledPointRefundService: CanceledPointRefundService,
) {

    /**
     * 상품 주문 취소 처리 메서드
     * - 사용자 권한 확인 후, 주문 상태 사용자 취소로 변경
     * - 해당 주문에 사용한 point usage 조회
     * - 사용한 포인트 환불 처리
     * - 상품 재고 업데이트
     *
     * @param userId 사용자 ID(PK)
     * @param orderId 상품 주문 ID(PK)
     */
    @Transactional
    fun cancelOrder(userId: Long, orderId: Long) {
        val order = orderService.readById(orderId)
            ?: throw OrderException(OrderErrorType.NOT_FOUND)

        if (!order.isOwner(userId)) {
            throw OrderException(OrderErrorType.NO_PERMISSION)
        }

        if (order.status != OrderStatus.COMPLETED) {
            throw OrderException(OrderErrorType.ALREADY_CANCELLED)
        }

        order.cancelByUser()

        // 해당 주문에 사용된 포인트 내역 조회 후 환불 처리
        val usages = orderPointUsageService.readByOrderId(orderId)
        refundUsedPoints(userId, usages)

        // 상품 재고 되돌리기
        productService.increaseStock(order.product.id!!, order.quantity)
    }

    /**
     * 사용한 포인트 환불 처리 메서드
     *
     * @param userId 사용자 ID(PK)
     * @param usages 사용한 포인트 리스트
     */
    private fun refundUsedPoints(
        userId: Long,
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
        for (usage in usages) {
            val pointRecordId = usage.pointRecord.id

            val pointRecord = pointRecordById[pointRecordId]
                ?: throw PointException(PointErrorType.NOT_FOUND)

            val usedAmount = usage.usedAmount

            if (pointRecord.status != PointStatus.CANCELED) {
                pointRecord.refundByUser(usedAmount)
                continue
            }

            canceledPointRefundService.refundCanceledPointFlow(userId, usedAmount)
        }
    }
}
