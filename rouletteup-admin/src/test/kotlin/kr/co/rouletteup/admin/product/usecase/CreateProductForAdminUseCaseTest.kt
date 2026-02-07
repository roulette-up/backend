package kr.co.rouletteup.admin.product.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.service.ProductService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
class CreateProductForAdminUseCaseTest {

    @MockK
    private lateinit var productService: ProductService

    @InjectMockKs
    private lateinit var createProductForAdminUseCase: CreateProductForAdminUseCase

    @Nested
    @DisplayName("상품 생성")
    inner class CreateProduct {

        @Test
        fun `상품 생성 요청이면 Product로 변환 후 저장한다`() {
            // given
            val request = mockk<AdminProductReq.Create>()
            val product = mockk<Product>(relaxed = true)

            every { request.toEntity() } returns product
            every { productService.save(product) } returns Unit

            // when
            createProductForAdminUseCase.createProduct(request)

            // then
            verify(exactly = 1) { request.toEntity() }
            verify(exactly = 1) { productService.save(product) }
        }
    }

}
