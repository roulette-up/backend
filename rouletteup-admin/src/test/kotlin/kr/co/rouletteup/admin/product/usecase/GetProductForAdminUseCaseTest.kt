package kr.co.rouletteup.admin.product.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlin.test.assertEquals
import kr.co.rouletteup.admin.product.dto.AdminProductDetail
import kr.co.rouletteup.admin.product.dto.AdminProductSummary
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
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
class GetProductForAdminUseCaseTest {

    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var getProductForAdminUseCase: GetProductForAdminUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(AdminProductSummary.Companion)
        mockkObject(AdminProductDetail.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(AdminProductSummary.Companion)
        unmockkObject(AdminProductDetail.Companion)
    }

    @Nested
    @DisplayName("전체 상품 조회")
    inner class GetProducts {

        @Test
        fun `전체 상품을 soft delete 포함 페이징 조회하고 AdminProductSummary로 변환한다`() {
            // given
            val pageable: Pageable = PageRequest.of(0, 20)

            val product1 = mockk<Product>(relaxed = true)
            val product2 = mockk<Product>(relaxed = true)

            val page = PageImpl(listOf(product1, product2), pageable, 2)

            val dto1 = mockk<AdminProductSummary>(relaxed = true)
            val dto2 = mockk<AdminProductSummary>(relaxed = true)

            every { productService.readAllIncludeDeleted(pageable) } returns page
            every { AdminProductSummary.from(product1) } returns dto1
            every { AdminProductSummary.from(product2) } returns dto2

            // when
            val result = getProductForAdminUseCase.getProducts(pageable)

            // then
            assertThat(result.content).hasSize(2)
            assertThat(result.content[0]).isSameAs(dto1)
            assertThat(result.content[1]).isSameAs(dto2)

            verify(exactly = 1) { productService.readAllIncludeDeleted(pageable) }
            verify(exactly = 1) { AdminProductSummary.from(product1) }
            verify(exactly = 1) { AdminProductSummary.from(product2) }
        }
    }

    @Nested
    @DisplayName("특정 상품 조회")
    inner class GetProductById {

        @Test
        fun `상품이 존재하면 soft delete 포함 단건 조회 후 AdminProductDetail로 변환한다`() {
            // given
            val productId = 1L
            val product = mockk<Product>(relaxed = true)
            val expected = mockk<AdminProductDetail>(relaxed = true)

            every { productService.readByIdIncludeDeleted(productId) } returns product
            every { AdminProductDetail.from(product) } returns expected

            // when
            val result = getProductForAdminUseCase.getProductById(productId)

            // then
            assertEquals(expected, result)

            verify(exactly = 1) { productService.readByIdIncludeDeleted(productId) }
            verify(exactly = 1) { AdminProductDetail.from(product) }
        }

        @Test
        fun `상품이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val productId = 999L
            every { productService.readByIdIncludeDeleted(productId) } returns null

            // when
            val exception = assertThrows<ProductException> {
                getProductForAdminUseCase.getProductById(productId)
            }

            // then
            assertEquals(ProductErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { productService.readByIdIncludeDeleted(productId) }
            verify(exactly = 0) { AdminProductDetail.from(any()) }
        }
    }
}