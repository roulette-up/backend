package kr.co.rouletteup.admin.product.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class UpdateProductUseCaseTest {

    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var updateProductUseCase: UpdateProductUseCase

    @Nested
    @DisplayName("상품 정보 업데이트")
    inner class UpdateProductInfo {

        @Test
        fun `상품이 존재하면 이름과 가격을 업데이트한다`() {
            // given
            val productId = 1L
            val request = mockk<AdminProductReq.Update>(relaxed = true)
            val product = mockk<Product>(relaxed = true)

            every { productService.readById(productId) } returns product

            // when
            updateProductUseCase.updateProductInfo(productId, request)

            // then
            verify(exactly = 1) { productService.readById(productId) }
            verify(exactly = 1) { product.updateInfo(request.name, request.price) }
        }

        @Test
        fun `상품이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val productId = 999L
            val request = mockk<AdminProductReq.Update>(relaxed = true)

            every { productService.readById(productId) } returns null

            // when
            val exception = assertThrows<ProductException> {
                updateProductUseCase.updateProductInfo(productId, request)
            }

            // then
            assertEquals(ProductErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { productService.readById(productId) }
            verify(exactly = 0) { request.name }
            verify(exactly = 0) { request.price }
        }
    }

    @Nested
    @DisplayName("상품 재고 업데이트")
    inner class UpdateProductStock {

        @Test
        fun `상품이 존재하면 재고를 증감한다`() {
            // given
            val productId = 1L
            val request = mockk<AdminProductReq.UpdateStock>(relaxed = true)
            val product = mockk<Product>(relaxed = true)

            every { productService.readById(productId) } returns product

            // when
            updateProductUseCase.updateProductStock(productId, request)

            // then
            verify(exactly = 1) { productService.readById(productId) }
            verify(exactly = 1) { product.changeStock(request.increaseStock) }
        }

        @Test
        fun `상품이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val productId = 999L
            val request = mockk<AdminProductReq.UpdateStock>(relaxed = true)

            every { productService.readById(productId) } returns null

            // when
            val exception = assertThrows<ProductException> {
                updateProductUseCase.updateProductStock(productId, request)
            }

            // then
            assertEquals(ProductErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { productService.readById(productId) }
            verify(exactly = 0) { request.increaseStock }
        }

        @Test
        fun `재고 감소 요청이 재고를 음수로 만들면 예외가 발생한다`() {
            // given
            val productId = 1L
            val request = mockk<AdminProductReq.UpdateStock>()
            every { request.increaseStock } returns -3

            val product = Product(
                name = "상품",
                price = 1000L,
                stockQuantity = 2,
            )

            every { productService.readById(productId) } returns product

            // when
            val exception = assertThrows<ProductException> {
                updateProductUseCase.updateProductStock(productId, request)
            }

            // then
            assertEquals(ProductErrorType.INSUFFICIENT_STOCK, exception.errorType)

            assertThat(product.stockQuantity).isEqualTo(2)
            verify(exactly = 1) { productService.readById(productId) }
        }
    }

}
