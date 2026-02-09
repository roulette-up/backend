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
import kr.co.rouletteup.domain.order.dto.OrderWithNicknameDto
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
    @DisplayName("사용자별 주문 내역 조회")
    inner class GetOrdersByUserId {

        @Test
        fun `userId로 페이징 조회하고 AdminOrderSummary로 변환한다`() {
            // given
            val userId = 1L
            val pageable: Pageable = PageRequest.of(0, 20)

            val order1 = mockk<OrderWithNicknameDto>(relaxed = true)
            val order2 = mockk<OrderWithNicknameDto>(relaxed = true)
            val page = PageImpl(listOf(order1, order2), pageable, 2)

            val dto1 = mockk<AdminOrderSummary>(relaxed = true)
            val dto2 = mockk<AdminOrderSummary>(relaxed = true)

            every { orderService.readAllWithNicknameByUserId(userId, pageable) } returns page
            every { AdminOrderSummary.from(order1) } returns dto1
            every { AdminOrderSummary.from(order2) } returns dto2

            // when
            val result = getOrderForAdminUseCase.getOrdersByUserId(userId, pageable)

            // then
            assertThat(result.content).hasSize(2)
            assertThat(result.content[0]).isSameAs(dto1)
            assertThat(result.content[1]).isSameAs(dto2)

            verify(exactly = 1) { orderService.readAllWithNicknameByUserId(userId, pageable) }
            verify(exactly = 1) { AdminOrderSummary.from(order1) }
            verify(exactly = 1) { AdminOrderSummary.from(order2) }
        }
    }

    @Nested
    @DisplayName("상품별 주문 내역 조회")
    inner class GetOrdersByProductId {

        @Test
        fun `productId로 해당 상품 주문만 페이징 조회하고 AdminOrderSummary로 변환한다`() {
            // given
            val productId = 1L
            val pageable: Pageable = PageRequest.of(0, 20)

            val order1 = mockk<OrderWithNicknameDto>(relaxed = true)
            val order2 = mockk<OrderWithNicknameDto>(relaxed = true)
            val page = PageImpl(listOf(order1, order2), pageable, 2)

            val dto1 = mockk<AdminOrderSummary>(relaxed = true)
            val dto2 = mockk<AdminOrderSummary>(relaxed = true)

            every { orderService.readAllWithNicknameByProductId(productId, pageable) } returns page
            every { AdminOrderSummary.from(order1) } returns dto1
            every { AdminOrderSummary.from(order2) } returns dto2

            // when
            val result = getOrderForAdminUseCase.getOrdersByProductId(productId, pageable)

            // then
            assertThat(result.content).hasSize(2)
            assertThat(result.content[0]).isSameAs(dto1)
            assertThat(result.content[1]).isSameAs(dto2)

            verify(exactly = 1) { orderService.readAllWithNicknameByProductId(productId, pageable) }
            verify(exactly = 1) { AdminOrderSummary.from(order1) }
            verify(exactly = 1) { AdminOrderSummary.from(order2) }
        }
    }

    @Nested
    @DisplayName("특정 주문 내역 조회")
    inner class GetOrderById {

        @Test
        fun `주문이 존재하면 조회 후 AdminOrderDetail로 변환해 반환한다`() {
            // given
            val orderId = 10L
            val order = mockk<OrderWithNicknameDto>(relaxed = true)
            val expected = mockk<AdminOrderDetail>(relaxed = true)

            every { orderService.readWithNicknameById(orderId) } returns order
            every { AdminOrderDetail.from(order) } returns expected

            // when
            val result = getOrderForAdminUseCase.getOrderById(orderId)

            // then
            assertEquals(expected, result)

            verify(exactly = 1) { orderService.readWithNicknameById(orderId) }
            verify(exactly = 1) { AdminOrderDetail.from(order) }
        }

        @Test
        fun `주문이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val orderId = 999L
            every { orderService.readWithNicknameById(orderId) } returns null

            // when
            val exception = assertThrows<OrderException> {
                getOrderForAdminUseCase.getOrderById(orderId)
            }

            // then
            assertEquals(OrderErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { orderService.readWithNicknameById(orderId) }
            verify(exactly = 0) { AdminOrderDetail.from(any()) }
        }
    }
}
