package kr.co.rouletteup.domain.point.exception

import kr.co.rouletteup.common.response.exception.BaseException

class PointException(
    override val errorType: PointErrorType,
) : BaseException(errorType)
