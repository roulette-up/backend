package kr.co.rouletteup.app.auth.usecase

import kr.co.rouletteup.app.auth.dto.SignInReq
import kr.co.rouletteup.app.auth.dto.SignInRes
import kr.co.rouletteup.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SignInUseCase(
    private val userService: UserService,
) {

    /**
     * 로그인 처리 메서드
     * - 이미 존재하는 사용자 → id 반환
     * - 존재하지 않는 사용자 → 저장 후 id 반환
     *
     * @param request 로그인 요청 dto
     * @return 사용자 id(PK)
     */
    @Transactional
    fun signIn(request: SignInReq): SignInRes {
        val user = userService.readByNickname(request.nickname)
            ?: userService.save(request.toEntity())

        return SignInRes.from(user)
    }
}
