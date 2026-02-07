package kr.co.rouletteup.app.order.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kr.co.rouletteup.app.order.dto.dto.OrderDetail
import kr.co.rouletteup.app.order.dto.dto.OrderSummary
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
class GetOrderUseCaseTest {

    @MockK
    private lateinit var orderService: OrderService

    @InjectMockKs
    private lateinit var getOrderUseCase: GetOrderUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(OrderSummary.Companion)
        mockkObject(OrderDetail.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(OrderSummary.Companion)
        unmockkObject(OrderDetail.Companion)
    }

    @Nested
    @DisplayName("주문 페이징 조회")
    inner class GetOrders {

        @Test
        fun `userId로 주문 목록을 페이징 조회하고 OrderSummary로 변환한다`() {
            // given
            val userId = 1L
            val pageable: Pageable = PageRequest.of(0, 20)

            val order1 = mockk<Order>(relaxed = true)
            val order2 = mockk<Order>(relaxed = true)
            val page = PageImpl(listOf(order1, order2), pageable, 2)

            val dto1 = mockk<OrderSummary>(relaxed = true)
            val dto2 = mockk<OrderSummary>(relaxed = true)

            every { orderService.readAllByUserId(userId, pageable) } returns page
            every { OrderSummary.from(order1) } returns dto1
            every { OrderSummary.from(order2) } returns dto2

            // when
            val result = getOrderUseCase.getOrders(userId, pageable)

            // then
            assertEquals(2, result.content.size)
            assertSame(dto1, result.content[0])
            assertSame(dto2, result.content[1])

            verify(exactly = 1) { orderService.readAllByUserId(userId, pageable) }
            verify(exactly = 1) { OrderSummary.from(order1) }
            verify(exactly = 1) { OrderSummary.from(order2) }
        }
    }

    @Nested
    @DisplayName("특정 주문 조회")
    inner class GetOrderById {

        @Test
        fun `주문이 존재하면 OrderDetail로 변환해 반환한다`() {
            // given
            val orderId = 10L
            val order = mockk<Order>(relaxed = true)
            val expected = mockk<OrderDetail>(relaxed = true)

            every { orderService.readById(orderId) } returns order
            every { OrderDetail.from(order) } returns expected

            // when
            val result = getOrderUseCase.getOrderById(orderId)

            // then
            assertSame(expected, result)
            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 1) { OrderDetail.from(order) }
        }

        @Test
        fun `주문이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val orderId = 999L
            every { orderService.readById(orderId) } returns null

            // when
            val exception = assertThrows(OrderException::class.java) {
                getOrderUseCase.getOrderById(orderId)
            }

            // then
            assertEquals(OrderErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { orderService.readById(orderId) }
            verify(exactly = 0) { OrderDetail.from(any()) }
        }
    }
}