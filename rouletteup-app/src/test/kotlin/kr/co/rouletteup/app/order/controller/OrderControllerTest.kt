package kr.co.rouletteup.app.order.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.Test
import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.app.order.usecase.PurchaseProductUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest(OrderController::class)
class OrderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var purchaseProductUseCase: PurchaseProductUseCase

    @Nested
    @DisplayName("상품 구매 API")
    inner class PurchaseProduct {

        @Test
        fun `성공 - 구매 요청하면 200과 성공 응답을 반환한다`() {
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
                    MockMvcResultMatchers.jsonPath("$.code").value(200)
                    MockMvcResultMatchers.jsonPath("$.message").value("요청이 성공하였습니다.")
                }
        }
    }
}
