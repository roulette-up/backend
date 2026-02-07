package kr.co.rouletteup.admin.product.usecase

import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.domain.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateProductForAdminUseCase(
    private val productService: ProductService,
) {

    /**
     * 상품 생성 메서드
     *
     * @param request 상품 생성 요청 DTO
     */
    @Transactional
    fun createProduct(request: AdminProductReq.Create) {
        productService.save(request.toEntity())
    }

}
