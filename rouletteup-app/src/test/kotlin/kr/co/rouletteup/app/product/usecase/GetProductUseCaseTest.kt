package kr.co.rouletteup.app.product.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertSame
import kr.co.rouletteup.app.product.dto.ProductDetail
import kr.co.rouletteup.app.product.dto.ProductSummary
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
class GetProductUseCaseTest {

    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var getProductUseCase: GetProductUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(ProductSummary.Companion)
        mockkObject(ProductDetail.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(ProductSummary.Companion)
        unmockkObject(ProductDetail.Companion)
    }

    @Nested
    @DisplayName("상품 페이징 조회")
    inner class GetProducts {

        @Test
        fun `상품을 페이징 조회하고 ProductSummary로 변환한다`() {
            // given
            val pageable: Pageable = PageRequest.of(0, 20)

            val product1 = mockk<Product>(relaxed = true)
            val product2 = mockk<Product>(relaxed = true)

            val page = PageImpl(listOf(product1, product2), pageable, 2)

            every { productService.readAll(pageable) } returns page

            val dto1 = mockk<ProductSummary>(relaxed = true)
            val dto2 = mockk<ProductSummary>(relaxed = true)
            every { ProductSummary.from(product1) } returns dto1
            every { ProductSummary.from(product2) } returns dto2

            // when
            val result = getProductUseCase.getProducts(pageable)

            // then
            assertEquals(2, result.content.size)
            assertSame(dto1, result.content[0])
            assertSame(dto2, result.content[1])

            verify(exactly = 1) { productService.readAll(pageable) }
            verify(exactly = 2) { ProductSummary.from(any()) }
        }
    }

    @Nested
    @DisplayName("특정 상품 조회")
    inner class GetProductById {

        @Test
        fun `상품이 존재하면 ProductDetail로 변환해 반환한다`() {
            // given
            val productId = 1L
            val product = mockk<Product>(relaxed = true)
            val expected = mockk<ProductDetail>(relaxed = true)

            every { productService.readById(productId) } returns product
            every { ProductDetail.from(product) } returns expected

            // when
            val result = getProductUseCase.getProductById(productId)

            // then
            assertSame(expected, result)

            verify(exactly = 1) { productService.readById(productId) }
            verify(exactly = 1) { ProductDetail.from(product) }
        }

        @Test
        fun `상품이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val productId = 999L
            every { productService.readById(productId) } returns null

            // when
            val exception = assertThrows(ProductException::class.java) {
                getProductUseCase.getProductById(productId)
            }

            // then
            assertEquals(ProductErrorType.NOT_FOUND, exception.errorType)
            verify(exactly = 1) { productService.readById(productId) }
        }
    }
}
