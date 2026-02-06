package kr.co.rouletteup.domain.user.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * User ErrorCode: UXXX
 */
enum class UserErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(StatusCode.NOT_FOUND, "U001", "사용자가 존재하지 않습니다."),
    ;

}
