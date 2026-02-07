package kr.co.rouletteup.admin.product.controller

import jakarta.validation.Valid
import kr.co.rouletteup.admin.product.api.AdminProductApi
import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.admin.product.usecase.CreateProductForAdminUseCase
import kr.co.rouletteup.admin.product.usecase.DeleteProductForAdminUseCase
import kr.co.rouletteup.admin.product.usecase.GetProductForAdminUseCase
import kr.co.rouletteup.admin.product.usecase.UpdateProductForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/products")
class AdminProductController(
    private val createProductForAdminUseCase: CreateProductForAdminUseCase,
    private val getProductForAdminUseCase: GetProductForAdminUseCase,
    private val updateProductForAdminUseCase: UpdateProductForAdminUseCase,
    private val deleteProductForAdminUseCase: DeleteProductForAdminUseCase,
) : AdminProductApi {

    @PostMapping
    override fun createProduct(
        @RequestBody @Valid request: AdminProductReq.Create,
    ): ResponseEntity<*> {
        createProductForAdminUseCase.createProduct(request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping
    override fun getProducts(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getProductForAdminUseCase.getProducts(pageable)
            )
        )

    @GetMapping("/{productId}")
    override fun getProductById(
        @PathVariable productId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getProductForAdminUseCase.getProductById(productId)
            )
        )

    @PutMapping("/{productId}")
    override fun updateProductInfo(
        @PathVariable productId: Long,
        @RequestBody @Valid request: AdminProductReq.Update,
    ): ResponseEntity<*> {
        updateProductForAdminUseCase.updateProductInfo(productId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @PatchMapping("/{productId}/stock")
    override fun updateProductStock(
        @PathVariable productId: Long,
        @RequestBody @Valid request: AdminProductReq.UpdateStock,
    ): ResponseEntity<*> {
        updateProductForAdminUseCase.updateProductStock(productId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{productId}")
    override fun deleteProduct(
        @PathVariable productId: Long,
    ): ResponseEntity<*> {
        deleteProductForAdminUseCase.deleteProduct(productId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

}
