package kr.co.rouletteup.admin.auth.usecase

import kr.co.rouletteup.admin.auth.dto.AdminSignInReq
import kr.co.rouletteup.admin.auth.dto.AdminSignInRes
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import kr.co.rouletteup.exception.AdminAuthErrorType
import kr.co.rouletteup.exception.AdminAuthException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignInForAdminUseCase(
    private val userService: UserService,
) {

    /**
     * 어드민 로그인 처리 메서드
     * - 어드민이 아니라면 로그인하지 못함
     *
     * @param request 어드민 로그인 요청 DTO
     * @return 어드민 정보 DTO
     */
    @Transactional(readOnly = true)
    fun signIn(request: AdminSignInReq): AdminSignInRes {
        val user = userService.readByNickname(request.nickname)
            ?: throw UserException(UserErrorType.NOT_FOUND)

        if (!user.isAdmin()) {
            throw AdminAuthException(AdminAuthErrorType.FORBIDDEN)
        }

        return AdminSignInRes.from(user)
    }
}