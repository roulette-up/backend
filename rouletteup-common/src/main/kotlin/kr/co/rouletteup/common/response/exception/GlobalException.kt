package kr.co.rouletteup.common.response.exception

import kr.co.rouletteup.common.response.error.type.GlobalErrorType

class GlobalException(
    override val errorType: GlobalErrorType,
) : BaseException(errorType)