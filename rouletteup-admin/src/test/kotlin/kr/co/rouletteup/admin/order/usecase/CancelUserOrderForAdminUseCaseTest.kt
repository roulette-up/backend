package kr.co.rouletteup.admin.order.usecase

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kr.co.rouletteup.admin.order.service.CanceledPointRefundService
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
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CancelUserOrderForAdminUseCaseTest {

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
    private lateinit var cancelUserOrderForAdminUseCase: CancelUserOrderForAdminUseCase

    @Nested
    @DisplayName("어드민 사용자 주문 내역 취소")
    inner class CancelUserOrder {

        @Test
        fun `성공 - 사용 포인트 내역이 있으면 포인트 환불 후 주문 취소 및 재고 복구를 수행한다`() {
            // given
            val orderId = 1L
            val order = mockk<Order>(relaxed = true)
            val product = mockk<Product>(relaxed = true)

            every { orderService.readById(orderId) } returns order
            every { order.status } returns OrderStatus.COMPLETED
            every { order.product } returns product
            every { product.id } returns 10L
            every { order.quantity } returns 2

            val usage1 = mockk<OrderPointUsage>(relaxed = true)
            val usage2 = mockk<OrderPointUsage>(relaxed = true)

            val pointRecord1 = mockk<PointRecord>(relaxed = true)
            val pointRecord2 = mockk<PointRecord>(relaxed = true)

            every { usage1.pointRecord.id } returns 100L
            every { usage1.usedAmount } returns 300L

            every { usage2.pointRecord.id } returns 101L
            every { usage2.usedAmount } returns 200L

            every { orderPointUsageService.readByOrderId(orderId) } returns listOf(usage1, usage2)
            every { productService.increaseStock(10, 2) } returns 1

            every { pointRecordService.readAllByIds(match { it.containsAll(listOf(100L, 101L)) }) } returns listOf(
                pointRecord1,
                pointRecord2
            )
            every { pointRecord1.id } returns 100L
            every { pointRecord2.id } returns 101L
            every { canceledPointRefundService.refundCanceledPointFlow(any(), any()) } just Runs

            // when
            cancelUserOrderForAdminUseCase.cancelUserOrder(orderId)

            // then
            verify(exactly = 1) { order.cancelByAdmin() }
            verify(exactly = 1) { orderPointUsageService.readByOrderId(orderId) }
            verify(exactly = 1) { pointRecord1.restore(300L) }
            verify(exactly = 1) { pointRecord2.restore(200L) }
            verify(exactly = 1) { productService.increaseStock(10L, 2) }
        }

        @Test
        fun `주문이 존재하지 않으면 NOT_FOUND 예외를 던진다`() {
            // given
            val orderId = 1L
            every { orderService.readById(orderId) } returns null

            // when
            val exception = assertThrows(OrderException::class.java) {
                cancelUserOrderForAdminUseCase.cancelUserOrder(orderId)
            }

            // then
            assertEquals(OrderErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 0) { orderPointUsageService.readByOrderId(any()) }
            verify(exactly = 0) { productService.increaseStock(any(), any()) }
        }

        @Test
        fun `주문 상태가 COMPLETED가 아니면 ALREADY_CANCELLED 예외를 던진다`() {
            // given
            val orderId = 1L
            val order = mockk<Order>(relaxed = true)

            every { orderService.readById(orderId) } returns order
            every { order.status } returns OrderStatus.USER_CANCELLED

            // when
            val exception = assertThrows(OrderException::class.java) {
                cancelUserOrderForAdminUseCase.cancelUserOrder(orderId)
            }

            // then
            assertEquals(OrderErrorType.ALREADY_CANCELLED, exception.errorType)

            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 0) { order.cancelByAdmin() }
            verify(exactly = 0) { orderPointUsageService.readByOrderId(any()) }
            verify(exactly = 0) { productService.increaseStock(any(), any()) }
        }
    }

}
