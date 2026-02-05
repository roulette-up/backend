package kr.co.rouletteup.domain.order.exception

import kr.co.rouletteup.common.response.exception.BaseException

class OrderException(
    override val errorType: OrderErrorType,
) : BaseException(errorType)
