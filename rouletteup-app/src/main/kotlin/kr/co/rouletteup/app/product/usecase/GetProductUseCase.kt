package kr.co.rouletteup.app.product.usecase

import kr.co.rouletteup.app.product.dto.ProductDetail
import kr.co.rouletteup.app.product.dto.ProductSummary
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProductUseCase(
    private val productService: ProductService,
) {

    /**
     * 상품 페이징 조회 메서드
     *
     * @param pageable 페이지 객체
     * @return 페이지 처리된 상품 DTO
     */
    @Transactional(readOnly = true)
    fun getProducts(pageable: Pageable): Page<ProductSummary> =
        productService.readAll(pageable)
            .map { product -> ProductSummary.from(product) }

    /**
     * 특정 상품 조회 메서드
     *
     * @param productId 상품 ID(PK)
     * @return 상품 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getProductById(productId: Long): ProductDetail {
        val product = productService.readById(productId)
            ?: throw ProductException(ProductErrorType.NOT_FOUND)

        return ProductDetail.from(product)
    }
}
