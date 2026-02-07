package kr.co.rouletteup.domain.point.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Point ErrorCode: PXXX
 */
enum class PointErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    ALREADY_PARTICIPATED(StatusCode.BAD_REQUEST, "P001", "금일은 이미 참여한 기록이 있습니다."),
    NOT_FOUND(StatusCode.NOT_FOUND, "P002", "포인트 기록이 존재하지 않습니다.")
    ;
}
