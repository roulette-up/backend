package kr.co.rouletteup.admin.product.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import kr.co.rouletteup.domain.product.entity.Product

class AdminProductReq {

    data class Create(

        @field:NotBlank(message = "상품명은 필수입니다.")
        @field:Size(min = 2, max = 100, message = "상품명은 2~100자여야 합니다.")
        val name: String,

        @field:NotNull(message = "가격은 필수입니다.")
        @field:Min(value = 1, message = "가격은 1 이상이어야 합니다.")
        val price: Long,

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

    data class Update(

        @field:NotBlank(message = "상품명은 필수입니다.")
        @field:Size(min = 2, max = 100, message = "상품명은 2~100자여야 합니다.")
        val name: String,

        @field:NotNull(message = "가격은 필수입니다.")
        @field:Min(value = 1, message = "가격은 1 이상이어야 합니다.")
        val price: Long,
    )

    data class UpdateStock(

        @field:NotNull(message = "업데이트 하고자 하는 수량은 필수입니다.")
        val increaseStock: Int,
    )
}