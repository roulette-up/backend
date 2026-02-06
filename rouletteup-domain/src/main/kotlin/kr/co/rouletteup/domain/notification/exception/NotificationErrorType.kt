package kr.co.rouletteup.domain.notification.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.type.BaseErrorType

/**
 * Notification ErrorCode: NXXX
 */
enum class NotificationErrorType(
    override val status: StatusCode,
    override val code: String,
    override val message: String,
) : BaseErrorType {

    NOT_FOUND(StatusCode.NOT_FOUND, "N001", "알림이 존재하지 않습니다."),
    ;
}