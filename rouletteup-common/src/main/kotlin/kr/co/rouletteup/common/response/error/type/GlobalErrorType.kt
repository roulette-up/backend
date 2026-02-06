package kr.co.rouletteup.common.response.error.type

import kr.co.rouletteup.common.response.StatusCode

enum class GlobalErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    INTERNAL_SERVER_ERROR(StatusCode.INTERNAL_SERVER_ERROR, "G001", "서버 내부 에러입니다. 관리자에게 문의하세요."),
    VALIDATION_ERROR(StatusCode.BAD_REQUEST, "G002", "유효성 검증에 실패하였습니다."),
    TOO_MANY_REQUESTS(StatusCode.TOO_MANY_REQUESTS, "G003", "요청이 많아 처리할 수 없습니다. 잠시 후 다시 시도해주세요."),
    ;
}
