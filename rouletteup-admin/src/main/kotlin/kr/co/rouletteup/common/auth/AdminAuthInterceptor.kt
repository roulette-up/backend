package kr.co.rouletteup.common.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.rouletteup.domain.user.service.UserService
import kr.co.rouletteup.exception.AdminAuthErrorType
import kr.co.rouletteup.exception.AdminAuthException
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AdminAuthInterceptor(
    private val userService: UserService,
) : HandlerInterceptor {

    companion object {
        private const val HEADER_USER_ID = "X-User-Id"
    }

    /**
     * 어드민 권한 확인 로직
     */
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val userId = request.getHeader(HEADER_USER_ID)?.toLongOrNull()
            ?: throw AdminAuthException(AdminAuthErrorType.FORBIDDEN)

        val user = userService.readById(userId)
            ?: throw AdminAuthException(AdminAuthErrorType.FORBIDDEN)

        if (!user.isAdmin()) {
            throw AdminAuthException(AdminAuthErrorType.FORBIDDEN)
        }

        return true
    }

}
