package kr.co.rouletteup.domain.product.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Product ErrorCode: PRXX
 */
enum class ProductErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(StatusCode.NOT_FOUND, "PR001", "상품이 존재하지 않습니다."),
    ;
}
