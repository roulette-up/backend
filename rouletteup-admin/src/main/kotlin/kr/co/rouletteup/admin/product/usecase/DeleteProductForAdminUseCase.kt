package kr.co.rouletteup.admin.product.usecase

import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeleteProductForAdminUseCase(
    private val productService: ProductService,
) {

    /**
     * 상품 삭제 메서드
     *
     * @param productId 상품 ID(PK)
     */
    @Transactional
    fun deleteProduct(productId: Long) {
        productService.deleteById(productId)
    }

}