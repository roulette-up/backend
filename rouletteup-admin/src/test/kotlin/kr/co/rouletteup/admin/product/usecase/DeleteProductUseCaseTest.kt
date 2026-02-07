package kr.co.rouletteup.admin.product.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kr.co.rouletteup.domain.product.service.ProductService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DeleteProductUseCaseTest {

    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var deleteProductUseCase: DeleteProductUseCase

    @Nested
    @DisplayName("상품 삭제")
    inner class DeleteProduct {

        @Test
        fun `상품 id로 삭제를 요청한다`() {
            // given
            val productId = 1L
            every { productService.deleteById(productId) } returns Unit

            // when
            deleteProductUseCase.deleteProduct(productId)

            // then
            verify(exactly = 1) { productService.deleteById(productId) }
        }

    }
}