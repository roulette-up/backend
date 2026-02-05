package kr.co.rouletteup.domain.roulette.exception

import kr.co.rouletteup.common.response.exception.BaseException

class RouletteException(
    override val errorType: RouletteErrorType,
) : BaseException(errorType)
