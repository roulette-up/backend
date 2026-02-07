package kr.co.rouletteup.admin.product.usecase

import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.domain.product.exception.ProductErrorType
import kr.co.rouletteup.domain.product.exception.ProductException
import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateProductForAdminUseCase(
    private val productService: ProductService,
) {

    /**
     * 상품 업데이트 메서드
     *
     * @param productId 상품 ID(PK)
     * @param request 상품 업데이트 요청 DTO
     */
    @Transactional
    fun updateProductInfo(productId: Long, request: AdminProductReq.Update) {
        val product = productService.readById(productId)
            ?: throw ProductException(ProductErrorType.NOT_FOUND)

        product.updateInfo(request.name, request.price)
    }

    /**
     * 상품 재고 업데이트 메서드
     * - (+/-) 증감형식으로 진행
     *
     * @param productId 상품 ID(PK)
     * @param request 상품 재고 업데이트 요청 DTO
     */
    @Transactional
    fun updateProductStock(productId: Long, request: AdminProductReq.UpdateStock) {
        val product = productService.readById(productId)
            ?: throw ProductException(ProductErrorType.NOT_FOUND)

        product.changeStock(request.increaseStock)
    }
}
