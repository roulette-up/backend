package kr.co.rouletteup.domain.roulette.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

enum class RouletteErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

}
