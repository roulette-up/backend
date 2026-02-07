package kr.co.rouletteup.app.order.usecase

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kr.co.rouletteup.app.order.service.CanceledPointRefundService
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderPointUsageService
import kr.co.rouletteup.domain.order.service.OrderService
import kr.co.rouletteup.domain.order.type.OrderStatus
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.service.ProductService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CancelOrderUseCaseTest {

    @MockK
    private lateinit var orderService: OrderService

    @MockK
    private lateinit var orderPointUsageService: OrderPointUsageService

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @MockK
    private lateinit var productService: ProductService

    @MockK
    private lateinit var canceledPointRefundService: CanceledPointRefundService

    @InjectMockKs
    private lateinit var cancelOrderUseCase: CancelOrderUseCase

    @Nested
    @DisplayName("주문 취소 처리")
    inner class CancelOrder {

        @Test
        fun `usedAmount만큼 환불 후 재고 복구`() {
            // given
            val userId = 1L
            val orderId = 10L

            val product = mockk<Product>(relaxed = true)
            every { product.id } returns 99L

            val order = mockk<Order>(relaxed = true)
            every { orderService.readById(orderId) } returns order
            every { order.isOwner(userId) } returns true
            every { order.status } returns OrderStatus.COMPLETED
            every { order.product } returns product
            every { order.quantity } returns 2
            every { order.cancelByUser() } just Runs

            val usedPointRecord = mockk<PointRecord>(relaxed = true)
            every { usedPointRecord.id } returns 100L

            val usagePointRecord = mockk<PointRecord>(relaxed = true)
            every { usagePointRecord.id } returns 100L

            val usage = mockk<OrderPointUsage>(relaxed = true)
            every { usage.pointRecord } returns usagePointRecord
            every { usage.usedAmount } returns 500L

            every { orderPointUsageService.readByOrderId(orderId) } returns listOf(usage)
            every { pointRecordService.readAllByIds(listOf(100L)) } returns listOf(usedPointRecord)

            every { usedPointRecord.refundByUser(500L) } just Runs
            every { productService.increaseStock(99L, 2) } returns 1

            // when
            cancelOrderUseCase.cancelOrder(userId, orderId)

            // then
            verify(exactly = 1) { order.cancelByUser() }
            verify(exactly = 1) { orderPointUsageService.readByOrderId(orderId) }
            verify(exactly = 1) { pointRecordService.readAllByIds(listOf(100L)) }
            verify(exactly = 1) { usedPointRecord.refundByUser(500L) }
            verify(exactly = 1) { productService.increaseStock(99L, 2) }
        }

        @Test
        fun `주문이 없으면 NOT_FOUND 예외`() {
            // given
            val userId = 1L
            val orderId = 10L
            every { orderService.readById(orderId) } returns null

            // when
            val exception = assertThrows(OrderException::class.java) {
                cancelOrderUseCase.cancelOrder(userId, orderId)
            }

            // then
            assertEquals(OrderErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 0) { orderPointUsageService.readByOrderId(any()) }
            verify(exactly = 0) { pointRecordService.readAllByIds(any()) }
            verify(exactly = 0) { productService.increaseStock(any(), any()) }
        }

        @Test
        fun `주문 소유자가 아니면 NO_PERMISSION 예외`() {
            // given
            val userId = 1L
            val orderId = 10L

            val order = mockk<Order>(relaxed = true)
            every { orderService.readById(orderId) } returns order
            every { order.isOwner(userId) } returns false

            // when
            val exception = assertThrows(OrderException::class.java) {
                cancelOrderUseCase.cancelOrder(userId, orderId)
            }

            // then
            assertEquals(OrderErrorType.NO_PERMISSION, exception.errorType)
            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 1) { order.isOwner(userId) }
            verify(exactly = 0) { order.cancelByUser() }
            verify(exactly = 0) { orderPointUsageService.readByOrderId(any()) }
            verify(exactly = 0) { productService.increaseStock(any(), any()) }
        }

        @Test
        fun `주문 상태가 COMPLETED가 아니면 ALREADY_CANCELLED 예외`() {
            // given
            val userId = 1L
            val orderId = 10L

            val order = mockk<Order>(relaxed = true)
            every { orderService.readById(orderId) } returns order
            every { order.isOwner(userId) } returns true
            every { order.status } returns OrderStatus.USER_CANCELLED

            // when
            val exception = assertThrows(OrderException::class.java) {
                cancelOrderUseCase.cancelOrder(userId, orderId)
            }

            // then
            assertEquals(OrderErrorType.ALREADY_CANCELLED, exception.errorType)
            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 1) { order.isOwner(userId) }
            verify(exactly = 1) { order.status }
            verify(exactly = 0) { order.cancelByUser() }
            verify(exactly = 0) { orderPointUsageService.readByOrderId(any()) }
            verify(exactly = 0) { productService.increaseStock(any(), any()) }
        }
    }
}
