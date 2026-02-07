package kr.co.rouletteup.admin.product.controller

import jakarta.validation.Valid
import kr.co.rouletteup.admin.product.dto.AdminProductReq
import kr.co.rouletteup.admin.product.usecase.CreateProductUseCase
import kr.co.rouletteup.admin.product.usecase.DeleteProductUseCase
import kr.co.rouletteup.admin.product.usecase.GetProductUseCase
import kr.co.rouletteup.admin.product.usecase.UpdateProductUseCase
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
    private val createProductUseCase: CreateProductUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
) {

    @PostMapping
    fun createProduct(
        @RequestBody @Valid request: AdminProductReq.Create,
    ): ResponseEntity<*> {
        createProductUseCase.createProduct(request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @GetMapping
    fun getProducts(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getProductUseCase.getProducts(pageable)
            )
        )

    @GetMapping("/{productId}")
    fun getProductById(
        @PathVariable productId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getProductUseCase.getProductById(productId)
            )
        )

    @PutMapping("/{productId}")
    fun updateProductInfo(
        @PathVariable productId: Long,
        @RequestBody @Valid request: AdminProductReq.Update,
    ): ResponseEntity<*> {
        updateProductUseCase.updateProductInfo(productId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @PatchMapping("/{productId}/stock")
    fun updateProductStock(
        @PathVariable productId: Long,
        @RequestBody @Valid request: AdminProductReq.UpdateStock,
    ): ResponseEntity<*> {
        updateProductUseCase.updateProductStock(productId, request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @DeleteMapping("/{productId}")
    fun deleteProduct(
        @PathVariable productId: Long,
    ): ResponseEntity<*> {
        deleteProductUseCase.deleteProduct(productId)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

}
