package kr.co.rouletteup.app.order.usecase

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlin.test.Test
import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderPointUsageService
import kr.co.rouletteup.domain.order.service.OrderService
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PurchaseProductUseCaseTest {

    @MockK
    private lateinit var orderService: OrderService

    @MockK
    private lateinit var productService: ProductService

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @MockK
    private lateinit var orderPointUsageService: OrderPointUsageService

    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var purchaseProductUseCase: PurchaseProductUseCase

    @Nested
    @DisplayName("상품 구매 처리")
    inner class PurchaseProduct {

        @Test
        fun `성공 - 포인트 충분, 재고 차감 성공, 주문 생성 후 만료 임박 포인트부터 차감하고 usage 저장`() {
            // given
            val userId = 1L
            val request = OrderReq(productId = 10L, price = 800L, quantity = 2)

            val pointRecord1 = pointRecord(remaining = 500L)
            val pointRecord2 = pointRecord(remaining = 1_500L)
            val points = listOf(pointRecord1, pointRecord2)

            val totalPrice = request.price * request.quantity

            every {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            } returns points

            every { productService.decreaseStock(request.productId, request.quantity) } returns 1

            val user = mockk<User>()
            val product = mockk<Product>()

            every { userService.readById(userId) } returns user
            every { productService.readById(request.productId) } returns product

            val savedOrder = mockk<Order>()
            every { orderService.save(any()) } returns savedOrder

            val usageSlot = slot<List<OrderPointUsage>>()
            every { orderPointUsageService.saveAll(capture(usageSlot)) } just Runs

            // PointRecord.use(used) 검증을 위해 stub
            every { pointRecord1.use(any()) } just Runs
            every { pointRecord2.use(any()) } just Runs

            // when
            purchaseProductUseCase.purchaseProduct(userId, request)

            // then
            verify(exactly = 1) {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            }
            verify(exactly = 1) { productService.decreaseStock(request.productId, request.quantity) }
            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 1) { productService.readById(request.productId) }
            verify(exactly = 1) { orderService.save(any()) }
            verify(exactly = 1) { orderPointUsageService.saveAll(any()) }

            verify(exactly = 1) { pointRecord1.use(minOf(500L, totalPrice)) }
            verify(exactly = 1) { pointRecord2.use(minOf(1_500L, totalPrice - 500L)) }

            // usage 저장 내역 검증
            val usages = usageSlot.captured
            assertThat(usages).hasSize(2)
            assertThat(usages[0].usedAmount).isEqualTo(500L)
            assertThat(usages[0].order).isEqualTo(savedOrder)
            assertThat(usages[0].pointRecord).isEqualTo(pointRecord1)

            assertThat(usages[1].usedAmount).isEqualTo(1_100L)
            assertThat(usages[1].order).isEqualTo(savedOrder)
            assertThat(usages[1].pointRecord).isEqualTo(pointRecord2)

        }

        @Test
        fun `예외 - 포인트가 부족하면 INSUFFICIENT_POINT`() {
            // given
            val userId = 1L
            val request = OrderReq(productId = 10L, price = 1_000L, quantity = 3)

            val pointRecord1 = pointRecord(remaining = 1_000L)
            val pointRecord2 = pointRecord(remaining = 500L)
            every {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            } returns listOf(pointRecord1, pointRecord2)

            // when
            val exception = assertThrows<OrderException> {
                purchaseProductUseCase.purchaseProduct(userId, request)
            }

            // then
            assertThat(exception.errorType).isEqualTo(OrderErrorType.INSUFFICIENT_POINT)

            verify(exactly = 1) {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            }
            verify(exactly = 0) { productService.decreaseStock(any(), any()) }
            verify(exactly = 0) { orderService.save(any()) }
            verify(exactly = 0) { orderPointUsageService.saveAll(any()) }
        }

        @Test
        fun `예외 - 재고 차감 실패하면 OUT_OF_STOCK`() {
            // given
            val userId = 1L
            val request = OrderReq(productId = 10L, price = 1_000L, quantity = 1)

            val pointRecord = pointRecord(remaining = 1_000L)
            every {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            } returns listOf(pointRecord)

            every { productService.decreaseStock(request.productId, request.quantity) } returns 0

            // when
            val exception = assertThrows<OrderException> {
                purchaseProductUseCase.purchaseProduct(userId, request)
            }

            // then
            assertThat(exception.errorType).isEqualTo(OrderErrorType.OUT_OF_STOCK)

            verify(exactly = 1) { productService.decreaseStock(request.productId, request.quantity) }
            verify(exactly = 0) { userService.readById(any()) }
            verify(exactly = 0) { productService.readById(any()) }
            verify(exactly = 0) { orderService.save(any()) }
            verify(exactly = 0) { orderPointUsageService.saveAll(any()) }
        }

        @Test
        fun `예외 - 사용자 없으면 NOT_FOUND`() {
            // given
            val userId = 1L
            val request = OrderReq(productId = 10L, price = 1_000L, quantity = 1)

            val pointRecord = pointRecord(remaining = 1_000L)
            every {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            } returns listOf(pointRecord)

            every { productService.decreaseStock(request.productId, request.quantity) } returns 1
            every { userService.readById(userId) } returns null

            // when
            val exception = assertThrows<UserException> {
                purchaseProductUseCase.purchaseProduct(userId, request)
            }

            // then
            assertThat(exception.errorType).isEqualTo(UserErrorType.NOT_FOUND)

            verify(exactly = 0) { orderService.save(any()) }
            verify(exactly = 0) { orderPointUsageService.saveAll(any()) }
        }

        @Test
        fun `예외 - 상품 없으면 NOT_FOUND`() {
            // given
            val userId = 1L
            val request = OrderReq(productId = 10L, price = 1_000L, quantity = 1)

            val pointRecord = pointRecord(remaining = 1_000L)
            every {
                pointRecordService.readAllByUserIdAndStatusOrderByExpiresAtAsc(userId, PointStatus.AVAILABLE)
            } returns listOf(pointRecord)

            every { productService.decreaseStock(request.productId, request.quantity) } returns 1
            every { userService.readById(userId) } returns mockk<User>()
            every { productService.readById(request.productId) } returns null

            // when
            val exception = assertThrows<ProductException> {
                purchaseProductUseCase.purchaseProduct(userId, request)
            }

            // then
            assertThat(exception.errorType).isEqualTo(ProductErrorType.NOT_FOUND)

            verify(exactly = 0) { orderService.save(any()) }
            verify(exactly = 0) { orderPointUsageService.saveAll(any()) }
        }
    }

    private fun pointRecord(remaining: Long): PointRecord {
        val pointRecord = mockk<PointRecord>()
        every { pointRecord.status } returns PointStatus.AVAILABLE
        every { pointRecord.remainingPoint } returns remaining
        return pointRecord
    }
}