package kr.co.rouletteup.admin.product.usecase

import kr.co.rouletteup.admin.product.dto.AdminProductDetail
import kr.co.rouletteup.admin.product.dto.AdminProductSummary
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetProductForAdminUseCase(
    private val productService: ProductService,
) {

    /**
     * 전체 상품 조회 메서드
     *
     * @param pageable 페이지 크기
     * @return 상품 페이징 DTO
     */
    @Transactional(readOnly = true)
    fun getProducts(pageable: Pageable): Page<AdminProductSummary> =
        productService.readAll(pageable)
            .map { product -> AdminProductSummary.from(product) }

    /**
     * 특정 상품 조회 메서드
     *
     * @param productId 상품 ID(PK)
     * @return 상품 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getProductById(productId: Long): AdminProductDetail {
        val product = productService.readById(productId)
            ?: throw ProductException(ProductErrorType.NOT_FOUND)

        return AdminProductDetail.from(product)
    }
}
