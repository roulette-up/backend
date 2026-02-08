package kr.co.rouletteup.exception

import kr.co.rouletteup.common.response.error.type.BaseErrorType
import kr.co.rouletteup.common.response.exception.BaseException

class AdminAuthException(
    override val errorType: BaseErrorType,
) : BaseException(errorType)
