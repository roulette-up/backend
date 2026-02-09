package kr.co.rouletteup.admin.product.controller

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.LocalDateTime
import kr.co.rouletteup.admin.product.dto.AdminProductDetail
import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.admin.product.dto.AdminProductSummary
import kr.co.rouletteup.admin.product.usecase.CreateProductForAdminUseCase
import kr.co.rouletteup.admin.product.usecase.DeleteProductForAdminUseCase
import kr.co.rouletteup.admin.product.usecase.GetProductForAdminUseCase
import kr.co.rouletteup.admin.product.usecase.UpdateProductForAdminUseCase
import kr.co.rouletteup.common.auth.AdminAuthInterceptor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminProductController::class)
class AdminProductControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var createProductForAdminUseCase: CreateProductForAdminUseCase

    @MockitoBean
    private lateinit var getProductForAdminUseCase: GetProductForAdminUseCase

    @MockitoBean
    private lateinit var updateProductForAdminUseCase: UpdateProductForAdminUseCase

    @MockitoBean
    private lateinit var deleteProductForAdminUseCase: DeleteProductForAdminUseCase

    @MockitoBean
    private lateinit var adminAuthInterceptor: AdminAuthInterceptor

    @BeforeEach
    fun setUp() {
        given(adminAuthInterceptor.preHandle(any(), any(), any())).willReturn(true)
    }

    @Nested
    @DisplayName("상품 생성 API")
    inner class CreateProduct {

        @Test
        fun `성공 - 상품 생성 요청이면 200을 반환한다`() {
            // given
            val request = AdminProductReq.Create(
                name = "상품1",
                price = 1000L,
                stockQuantity = 10,
            )
            willDoNothing().given(createProductForAdminUseCase).createProduct(request)

            // when
            val resultActions = mockMvc.post("/api/v1/admin/products") {
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

        @Test
        fun `실패 - 제약 조건을 만족하지 않으면 400을 반환한다`() {
            // given
            val req = AdminProductReq.Create(
                name = "a",
                price = 0L,
                stockQuantity = 0,
            )

            // when
            val resultActions = mockMvc.post("/api/v1/admin/products") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(req)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.code").value("G002")
                    jsonPath("$.message").value("유효성 검증에 실패하였습니다.")
                    jsonPath("$.errors.name").value("상품명은 2~100자여야 합니다.")
                    jsonPath("$.errors.price").value("가격은 1 이상이어야 합니다.")
                    jsonPath("$.errors.stockQuantity").value("재고는 1 이상이어야 합니다.")
                }
        }
    }

    @Nested
    @DisplayName("상품 목록 조회 API")
    inner class GetProducts {

        @Test
        fun `성공 - 상품 목록을 페이징 조회하면 200을 반환한다`() {
            // given
            val pageable = PageRequest.of(0, 20)
            val page = PageImpl(emptyList<AdminProductSummary>(), pageable, 0)

            given(getProductForAdminUseCase.getProducts(any()))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/products") {
                param("page", "0")
                param("size", "20")
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.content").isArray()
                }
        }
    }

    @Nested
    @DisplayName("상품 단건 조회 API")
    inner class GetProductById {

        @Test
        fun `성공 - 상품 id로 단건 조회하면 200을 반환한다`() {
            // given
            val productId = 1L
            val response = AdminProductDetail(
                id = 1L,
                name = "상품1",
                stockQuantity = 1,
                price = 1_000L,
                createdAt = LocalDateTime.now(),
            )

            given(getProductForAdminUseCase.getProductById(productId))
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/products/$productId")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.id").value(productId)
                }
        }
    }

    @Nested
    @DisplayName("상품 정보 수정 API")
    inner class UpdateProductInfo {

        @Test
        fun `성공 - 상품 정보 수정 요청이면 200을 반환한다`() {
            // given
            val productId = 1L
            val request = AdminProductReq.Update(
                name = "상품1-수정",
                price = 2000L,
            )

            willDoNothing().given(updateProductForAdminUseCase).updateProductInfo(productId, request)

            // when
            val resultActions = mockMvc.put("/api/v1/admin/products/$productId") {
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

        @Test
        fun `실패 - 제약 조건을 만족하지 않으면 400을 반환한다`() {
            val productId = 1L
            val req = AdminProductReq.Update(
                name = "a",
                price = 0L,
            )

            // when
            val resultActions = mockMvc.put("/api/v1/admin/products/$productId") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(req)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.code").value("G002")
                    jsonPath("$.message").value("유효성 검증에 실패하였습니다.")
                    jsonPath("$.errors.name").value("상품명은 2~100자여야 합니다.")
                    jsonPath("$.errors.price").value("가격은 1 이상이어야 합니다.")
                }

        }
    }

    @Nested
    @DisplayName("상품 재고 증감 API")
    inner class UpdateProductStock {

        @Test
        fun `성공 - 상품 재고 증감 요청이면 200을 반환한다`() {
            // given
            val productId = 1L
            val request = AdminProductReq.UpdateStock(increaseStock = 3)

            willDoNothing().given(updateProductForAdminUseCase).updateProductStock(productId, request)

            // when
            val resultActions = mockMvc.patch("/api/v1/admin/products/$productId/stock") {
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
    @DisplayName("상품 삭제 API")
    inner class DeleteProduct {

        @Test
        fun `성공 - 상품 삭제 요청이면 200을 반환한다`() {
            // given
            val productId = 1L
            willDoNothing().given(deleteProductForAdminUseCase).deleteProduct(productId)

            // when
            val resultActions = mockMvc.delete("/api/v1/admin/products/$productId")

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
