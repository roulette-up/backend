package kr.co.rouletteup.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Admin ErrorCode: ADXXX
 */
enum class AdminAuthErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    FORBIDDEN(StatusCode.FORBIDDEN, "AD001", "관리자 권한이 필요합니다."),
    ;
}