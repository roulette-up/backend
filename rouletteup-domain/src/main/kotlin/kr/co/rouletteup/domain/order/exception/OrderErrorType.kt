package kr.co.rouletteup.domain.order.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Order ErrorCode: ORXXX
 */
enum class OrderErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    INSUFFICIENT_POINT(StatusCode.BAD_REQUEST, "OR001", "포인트가 부족합니다."),
    OUT_OF_STOCK(StatusCode.BAD_REQUEST, "OR002", "상품 재고가 부족합니다."),
    ;
}
