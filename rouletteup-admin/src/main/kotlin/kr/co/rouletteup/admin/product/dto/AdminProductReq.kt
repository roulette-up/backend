package kr.co.rouletteup.admin.product.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kr.co.rouletteup.domain.product.entity.Product

class AdminProductReq {

    @Schema(name = "상품 생성 요청 DTO")
    data class Create(

        @field:Schema(
            description = "상품명",
            example = "상품A",
            minLength = 2,
            maxLength = 100,
            required = true
        )
        @field:NotBlank(message = "상품명은 필수입니다.")
        @field:Size(min = 2, max = 100, message = "상품명은 2~100자여야 합니다.")
        val name: String,

        @field:Schema(
            description = "상품 가격",
            example = "4500",
            minimum = "1",
            required = true
        )
        @field:NotNull(message = "가격은 필수입니다.")
        @field:Min(value = 1, message = "가격은 1 이상이어야 합니다.")
        val price: Long,

        @field:Schema(
            description = "재고 수량",
            example = "50",
            minimum = "1",
            required = true
        )
        @field:NotNull(message = "재고는 필수입니다.")
        @field:Min(value = 1, message = "재고는 1 이상이어야 합니다.")
        val stockQuantity: Int,
    ) {

        fun toEntity(): Product =
            Product(
                name = name,
                price = price,
                stockQuantity = stockQuantity,
            )
    }

    @Schema(name = "상품 정보 업데이트 요청 DTO")
    data class Update(

        @field:Schema(
            description = "상품명",
            example = "상품A",
            minLength = 2,
            maxLength = 100,
            required = true
        )
        @field:NotBlank(message = "상품명은 필수입니다.")
        @field:Size(min = 2, max = 100, message = "상품명은 2~100자여야 합니다.")
        val name: String,

        @field:Schema(
            description = "상품 가격",
            example = "4500",
            minimum = "1",
            required = true
        )
        @field:NotNull(message = "가격은 필수입니다.")
        @field:Min(value = 1, message = "가격은 1 이상이어야 합니다.")
        val price: Long,
    )

    @Schema(name = "상품 재고 업데이트 요청 DTO")
    data class UpdateStock(

        @field:Schema(
            description = """
                재고 증감 수량  
                - 양수: 재고 증가  
                - 음수: 재고 감소
            """,
            example = "10",
            requiredMode = Schema.RequiredMode.REQUIRED
        )
        @field:NotNull(message = "업데이트 하고자 하는 수량은 필수입니다.")
        val increaseStock: Int,
    )
}