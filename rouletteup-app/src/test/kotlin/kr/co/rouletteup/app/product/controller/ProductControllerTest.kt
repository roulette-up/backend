package kr.co.rouletteup.app.product.controller

import java.time.LocalDateTime
import kotlin.test.Test
import kr.co.rouletteup.app.product.dto.ProductDetail
import kr.co.rouletteup.app.product.usecase.GetProductUseCase
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(ProductController::class)
class ProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getProductUseCase: GetProductUseCase

    @Nested
    @DisplayName("상품 페이징 조회 API")
    inner class GetProducts {

        @Test
        fun `기본 pageable(id desc)로 상품 목록을 조회한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 10)

            given(getProductUseCase.getProducts(any()))
                .willReturn(PageImpl(emptyList(), pageable, 0))

            // when
            val resultActions = mockMvc.get("/api/v1/products")

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
    @DisplayName("특정 상품 조회 API")
    inner class GetProductById {

        @Test
        fun `상품 단건 조회 시 200을 반환한다`() {
            // given
            val productId = 1L
            val response = ProductDetail(
                id = 1L,
                name = "상품명",
                stockQuantity = 3,
                price = 1000L,
                createdAt = LocalDateTime.now(),
                modifiedAt = LocalDateTime.now()
            )


            given(getProductUseCase.getProductById(productId)).willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/products/$productId")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data").exists()
                    jsonPath("$.data.id").value(1)
                }
        }
    }
}
