package kr.co.rouletteup.domain.roulette.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Roulette ErrorCode: RXXX
 *
 */
enum class RouletteErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(StatusCode.NOT_FOUND, "R001", "룰렛이 존재하지 않습니다."),
    ;
}
