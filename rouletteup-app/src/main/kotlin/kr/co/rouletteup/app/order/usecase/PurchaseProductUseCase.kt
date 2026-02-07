package kr.co.rouletteup.app.order.usecase

import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderPointUsageService
import kr.co.rouletteup.domain.order.service.OrderService
import kr.co.rouletteup.domain.order.type.OrderStatus
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PurchaseProductUseCase(
    private val orderService: OrderService,
    private val productService: ProductService,
    private val pointRecordService: PointRecordService,
    private val orderPointUsageService: OrderPointUsageService,
    private val userService: UserService,
) {

    /**
     * 상품 주문 처리 메서드
     * - 사용자의 포인트 내역을 들고와 구매 가능한지 확인
     * - 구매 가능하다면, 상품 재고 업데이트
     * - 주문 처리 후, 만료 기간이 빠른 포인트부터 사용 처리
     * - 상품 주문에 사용한 포인트 내역 저장 (OrderPointUsage)
     *
     * @param userId 사용자 ID(PK)
     * @param request 구매하고자 하는 상품 정보
     */
    @Transactional
    fun purchaseProduct(userId: Long, request: OrderReq) {
        val productId = request.productId
        val price = request.price
        val quantity = request.quantity

        val points = pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(
            userId, PointStatus.AVAILABLE
        )

        // 포인트 합계를 통해 구매 가능 확인
        val totalPoint = points.sumOf(PointRecord::remainingPoint)
        val totalPrice = price * quantity
        if (totalPoint < totalPrice) {
            throw OrderException(OrderErrorType.INSUFFICIENT_POINT)
        }

        // 상품 재고 업데이트
        val stockUpdated = productService.decreaseStock(productId, quantity)
        if (stockUpdated == 0) {
            throw OrderException(OrderErrorType.OUT_OF_STOCK)
        }

        // 주문 처리 후, 만료 기한이 가까운 포인트 사용
        val order = createOrder(userId, productId, quantity, price)
        val orderPointUsage = buildUsagePlan(order, points, totalPrice)

        orderPointUsageService.saveAll(orderPointUsage)
    }

    /**
     * 주문 생성 메서드
     *
     * @param userId 사용자 ID(PK)
     * @param productId 상품 ID(PK)
     * @param quantity 재고량
     * @param price 상품 가격
     * @return 주문 생성한 래코드
     */
    private fun createOrder(
        userId: Long,
        productId: Long,
        quantity: Int,
        price: Long,
    ): Order {
        val user = userService.readById(userId)
            ?: throw UserException(UserErrorType.NOT_FOUND)

        val product = productService.readById(productId)
            ?: throw ProductException(ProductErrorType.NOT_FOUND)

        val order = Order(
            quantity = quantity,
            productPrice = price,
            status = OrderStatus.COMPLETED,
            user = user,
            product = product
        )

        return orderService.save(order)
    }

    /**
     * 사용할 포인트 처리 메서드
     * - 만료 기한이 짧은 포인트부터 처리
     * - 더티체킹을 사용해 포인트 사용
     * - 사용한 포인트는 OrderPointUsage 객체를 만들어 반환
     *
     * @param order 상품 레코드
     * @param points 사용자 포인트 리스트
     * @param totalPrice 상품 총 가격
     * @return 사용한 포인트 내역
     */
    private fun buildUsagePlan(
        order: Order,
        points: List<PointRecord>,
        totalPrice: Long,
    ): List<OrderPointUsage> {
        var remainingToPay = totalPrice
        val usages = mutableListOf<OrderPointUsage>()

        // 사용할 포인트 선택
        for (point in points) {
            if (remainingToPay == 0L) {
                break
            }

            if (point.status != PointStatus.AVAILABLE || point.remainingPoint <= 0L) {
                continue
            }

            val used = minOf(point.remainingPoint, remainingToPay)

            // PointRecord 더티체킹 (remaining 감소, 0이면 USED 전환)
            point.use(used)

            // 처리된 것마다 OrderPointUsage 생성해서 리스트에 넣기
            usages += OrderPointUsage(
                usedAmount = used,
                order = order,
                pointRecord = point
            )

            remainingToPay -= used
        }

        return usages
    }

}
