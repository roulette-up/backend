package kr.co.rouletteup.common.response.exception

import kr.co.rouletteup.common.response.error.type.BaseErrorType

abstract class BaseException(
    open val errorType: BaseErrorType,
) : RuntimeException(errorType.message)
