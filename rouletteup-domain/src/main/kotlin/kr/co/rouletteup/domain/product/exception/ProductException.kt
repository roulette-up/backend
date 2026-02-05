package kr.co.rouletteup.domain.product.exception

import kr.co.rouletteup.common.response.exception.BaseException

class ProductException(
    override val errorType: ProductErrorType,
) : BaseException(errorType)
