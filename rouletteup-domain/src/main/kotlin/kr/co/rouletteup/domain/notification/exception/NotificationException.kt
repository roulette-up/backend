package kr.co.rouletteup.domain.notification.exception

import kr.co.rouletteup.common.response.exception.BaseException

class NotificationException(
    override val errorType: NotificationErrorType,
) : BaseException(errorType)