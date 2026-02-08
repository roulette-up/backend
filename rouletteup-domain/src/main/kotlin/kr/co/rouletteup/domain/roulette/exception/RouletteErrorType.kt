package kr.co.rouletteup.domain.roulette.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Roulette ErrorCode: RXXX
 */
enum class RouletteErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(StatusCode.NOT_FOUND, "R001", "룰렛이 존재하지 않습니다."),
    BUDGET_EXHAUSTED(StatusCode.BAD_REQUEST, "R002", "금일 룰렛 예산이 모두 소진되었습니다."),
    TODAY_BUDGET_ONLY_INCREASE(StatusCode.BAD_REQUEST, "R003", "금일 예산은 증가만 가능합니다."),
    FUTURE_DATE_ONLY(StatusCode.BAD_REQUEST, "R004", "이후 날짜만 설정 가능합니다."),
    ;
}
