package kr.co.rouletteup.domain.user.exception

import kr.co.rouletteup.common.response.exception.BaseException

class UserException(
    override val errorType: UserErrorType,
) : BaseException(errorType)
