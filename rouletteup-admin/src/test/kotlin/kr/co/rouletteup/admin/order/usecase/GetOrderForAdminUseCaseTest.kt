package kr.co.rouletteup.admin.order.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlin.test.assertEquals
import kr.co.rouletteup.admin.order.dto.AdminOrderDetail
import kr.co.rouletteup.admin.order.dto.AdminOrderSummary
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.exception.OrderErrorType
import kr.co.rouletteup.domain.order.exception.OrderException
import kr.co.rouletteup.domain.order.service.OrderService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
class GetOrderForAdminUseCaseTest {

    @MockK
    private lateinit var orderService: OrderService

    @InjectMockKs
    private lateinit var getOrderForAdminUseCase: GetOrderForAdminUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(AdminOrderSummary.Companion)
        mockkObject(AdminOrderDetail.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(AdminOrderSummary.Companion)
        unmockkObject(AdminOrderDetail.Companion)
    }

    @Nested
    @DisplayName("주문 내역 페이징 조회")
    inner class GetOrders {

        @Test
        fun `userId가 있으면 해당 유저 주문만 soft delete 포함 페이징 조회하고 AdminOrderSummary로 변환한다`() {
            // given
            val userId = 1L
            val pageable: Pageable = PageRequest.of(0, 20)

            val order1 = mockk<Order>(relaxed = true)
            val order2 = mockk<Order>(relaxed = true)
            val page = PageImpl(listOf(order1, order2), pageable, 2)

            val dto1 = mockk<AdminOrderSummary>(relaxed = true)
            val dto2 = mockk<AdminOrderSummary>(relaxed = true)

            every { orderService.readAllByUserIdIncludeDeleted(userId, pageable) } returns page
            every { AdminOrderSummary.from(order1) } returns dto1
            every { AdminOrderSummary.from(order2) } returns dto2

            // when
            val result = getOrderForAdminUseCase.getOrders(userId, pageable)

            // then
            assertThat(result.content).hasSize(2)
            assertThat(result.content[0]).isSameAs(dto1)
            assertThat(result.content[1]).isSameAs(dto2)

            verify(exactly = 1) { orderService.readAllByUserIdIncludeDeleted(userId, pageable) }
            verify(exactly = 0) { orderService.readAllIncludeDeleted(any()) }
            verify(exactly = 1) { AdminOrderSummary.from(order1) }
            verify(exactly = 1) { AdminOrderSummary.from(order2) }
        }

        @Test
        fun `userId가 null이면 전체 주문을 soft delete 포함 페이징 조회하고 AdminOrderSummary로 변환한다`() {
            // given
            val pageable: Pageable = PageRequest.of(0, 20)

            val order = mockk<Order>(relaxed = true)
            val page = PageImpl(listOf(order), pageable, 1)

            val dto = mockk<AdminOrderSummary>(relaxed = true)

            every { orderService.readAllIncludeDeleted(pageable) } returns page
            every { AdminOrderSummary.from(order) } returns dto

            // when
            val result = getOrderForAdminUseCase.getOrders(null, pageable)

            // then
            assertThat(result.content).hasSize(1)
            assertThat(result.content[0]).isSameAs(dto)

            verify(exactly = 0) { orderService.readAllByUserIdIncludeDeleted(any(), any()) }
            verify(exactly = 1) { orderService.readAllIncludeDeleted(pageable) }
            verify(exactly = 1) { AdminOrderSummary.from(order) }
        }
    }

    @Nested
    @DisplayName("특정 주문 내역 조회")
    inner class GetOrderById {

        @Test
        fun `주문이 존재하면 soft delete 포함 조회 후 AdminOrderDetail로 변환해 반환한다`() {
            // given
            val orderId = 10L
            val order = mockk<Order>(relaxed = true)
            val expected = mockk<AdminOrderDetail>(relaxed = true)

            every { orderService.readByIdIncludeDeleted(orderId) } returns order
            every { AdminOrderDetail.from(order) } returns expected

            // when
            val result = getOrderForAdminUseCase.getOrderById(orderId)

            // then
            assertEquals(expected, result)

            verify(exactly = 1) { orderService.readByIdIncludeDeleted(orderId) }
            verify(exactly = 1) { AdminOrderDetail.from(order) }
        }

        @Test
        fun `주문이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val orderId = 999L
            every { orderService.readByIdIncludeDeleted(orderId) } returns null

            // when
            val exception = assertThrows<OrderException> {
                getOrderForAdminUseCase.getOrderById(orderId)
            }

            // then
            assertEquals(OrderErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { orderService.readByIdIncludeDeleted(orderId) }
            verify(exactly = 0) { AdminOrderDetail.from(any()) }
        }
    }
}
