package kr.co.rouletteup.admin.order.controller

import java.time.LocalDateTime
import kr.co.rouletteup.admin.order.dto.AdminOrderDetail
import kr.co.rouletteup.admin.order.dto.AdminOrderSummary
import kr.co.rouletteup.admin.order.usecase.CancelUserOrderForAdminUseCase
import kr.co.rouletteup.admin.order.usecase.GetOrderForAdminUseCase
import kr.co.rouletteup.domain.order.type.OrderStatus
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminOrderController::class)
class AdminOrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getOrderForAdminUseCase: GetOrderForAdminUseCase

    @MockitoBean
    private lateinit var cancelUserOrderForAdminUseCase: CancelUserOrderForAdminUseCase

    @Nested
    @DisplayName("사용자별 주문 내역 조회 API")
    inner class GetOrdersByUserId {

        @Test
        fun `userId로 특정 사용자 주문만 조회한다`() {
            // given
            val userId = 5L
            val pageable = PageRequest.of(0, 20)

            val response = AdminOrderSummary(
                id = 1L,
                quantity = 2,
                productPrice = 100,
                productName = "상품1",
                status = OrderStatus.COMPLETED,
                productId = 10L,
                userId = 1L,
                deletedAt = null
            )

            val page = PageImpl(listOf(response), pageable, 1)

            given(getOrderForAdminUseCase.getOrdersByUserId(userId, pageable))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/users/$userId/orders") {
                param("page", "0")
                param("size", "20")
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.content[0].id").value(response.id)
                    jsonPath("$.data.content[0].status").value(response.status)
                }
        }
    }

    @Nested
    @DisplayName("상품별 주문 내역 조회 API")
    inner class GetOrders {
        @Test
        fun `productId로 특정 상품 주문만 조회한다`() {
            // given
            val productId = 5L
            val pageable = PageRequest.of(0, 20)

            val response = AdminOrderSummary(
                id = 1L,
                quantity = 2,
                productPrice = 100,
                productName = "상품1",
                status = OrderStatus.COMPLETED,
                productId = 10L,
                userId = 1L,
                deletedAt = null
            )

            val page = PageImpl(listOf(response), pageable, 1)

            given(getOrderForAdminUseCase.getOrdersByProductId(productId, pageable))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/products/$productId/orders") {
                param("page", "0")
                param("size", "20")
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.data.content[0].id").value(response.id)
                    jsonPath("$.data.content[0].status").value(response.status)
                }
        }
    }

    @Nested
    @DisplayName("특정 주문 조회 API")
    inner class GetOrderById {

        @Test
        fun `orderId로 주문 단건 조회`() {
            // given
            val orderId = 1L

            val response = AdminOrderDetail(
                id = 1L,
                quantity = 2,
                productPrice = 100,
                productName = "상품1",
                status = OrderStatus.COMPLETED,
                productId = 10L,
                createdAt = LocalDateTime.now(),
                userId = 1L,
                deletedAt = null
            )

            given(getOrderForAdminUseCase.getOrderById(orderId))
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/orders/$orderId") {
                accept = MediaType.APPLICATION_JSON
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.id").value(response.id)
                    jsonPath("$.data.productName").value(response.productName)
                    jsonPath("$.data.status").value(response.status)
                }
        }
    }

    @Nested
    @DisplayName("어드민 사용자 주문 내역 취소 API")
    inner class CancelUserOrder {

        @Test
        fun `성공 - 주문 취소 요청이면 200을 반환한다`() {
            // given
            val orderId = 1L
            willDoNothing().given(cancelUserOrderForAdminUseCase).cancelUserOrder(orderId)

            // when
            val resultActions = mockMvc.patch("/api/v1/admin/orders/$orderId/cancel")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                }
        }
    }
}
