package kr.co.rouletteup.app.order.controller

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kotlin.test.Test
import kr.co.rouletteup.app.auth.controller.AuthController
import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.app.order.dto.dto.OrderDetail
import kr.co.rouletteup.app.order.dto.dto.OrderSummary
import kr.co.rouletteup.app.order.usecase.CancelOrderUseCase
import kr.co.rouletteup.app.order.usecase.GetOrderUseCase
import kr.co.rouletteup.app.order.usecase.PurchaseProductUseCase
import kr.co.rouletteup.domain.order.type.OrderStatus
import kr.co.rouletteup.internal.filter.AdminInternalAuthFilter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(
    controllers = [OrderController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [AdminInternalAuthFilter::class]
        )
    ]
)
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var purchaseProductUseCase: PurchaseProductUseCase

    @MockitoBean
    private lateinit var getOrderUseCase: GetOrderUseCase

    @MockitoBean
    private lateinit var cancelOrderUseCase: CancelOrderUseCase

    @Nested
    @DisplayName("상품 구매 API")
    inner class PurchaseProduct {

        @Test
        fun `구매 요청하면 200과 성공 응답을 반환한다`() {
            // given
            val userId = 1L
            val request = OrderReq(
                productId = 10L,
                price = 1_000L,
                quantity = 2
            )

            willDoNothing().given(purchaseProductUseCase)
                .purchaseProduct(eq(userId), any())

            // when
            val resultActions = mockMvc.post("/api/v1/orders") {
                header("X-User-Id", userId)
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

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

    @Nested
    @DisplayName("주문 내역 리스트 조회 API")
    inner class GetOrders {

        @Test
        fun `기본 pageable로 주문 목록 조회`() {
            // given
            val userId = 1L

            given(getOrderUseCase.getOrders(any(), any()))
                .willReturn(PageImpl(emptyList<OrderSummary>(), PageRequest.of(0, 20), 0))

            // when
            val resultActions = mockMvc.get("/api/v1/orders/my") {
                header("X-User-Id", userId)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data").exists()
                }
        }
    }


    @Nested
    @DisplayName("특정 주문 내역 조회 API")
    inner class GetOrderById {

        @Test
        fun `주문 단건 조회`() {
            // given
            val orderId = 10L
            val response = OrderDetail(
                id = 1L,
                quantity = 3,
                productPrice = 300L,
                productName = "상품1",
                status = OrderStatus.COMPLETED,
                productId = 1L,
                createdAt = LocalDateTime.now()
            )

            given(getOrderUseCase.getOrderById(orderId))
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/orders/$orderId")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.id").value(orderId)
                }
        }
    }


    @Nested
    @DisplayName("주문 취소 처리 API")
    inner class CancelOrder {

        @Test
        fun `주문 취소`() {
            // given
            val userId = 1L
            val orderId = 20L

            willDoNothing().given(cancelOrderUseCase)
                .cancelOrder(userId, orderId)

            // when
            val resultActions = mockMvc.patch("/api/v1/orders/$orderId/cancel") {
                header("X-User-Id", userId)
            }

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
