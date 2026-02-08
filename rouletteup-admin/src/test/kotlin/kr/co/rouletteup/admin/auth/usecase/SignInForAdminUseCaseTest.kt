package kr.co.rouletteup.admin.auth.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kr.co.rouletteup.admin.auth.dto.AdminSignInReq
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import kr.co.rouletteup.domain.user.type.Role
import kr.co.rouletteup.exception.AdminAuthErrorType
import kr.co.rouletteup.exception.AdminAuthException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SignInForAdminUseCaseTest {

    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var signInForAdminUseCase: SignInForAdminUseCase

    @Nested
    @DisplayName("어드민 로그인")
    inner class SignIn {

        @Test
        fun `성공 - ADMIN 유저면 AdminSignInRes를 반환한다`() {
            // given
            val nickname = "admin"
            val req = AdminSignInReq(nickname = nickname)

            val user = mockk<User>()
            every { user.id } returns 1L
            every { user.nickname } returns nickname
            every { user.isAdmin() } returns true

            every { userService.readByNickname(nickname) } returns user

            // when
            val result = signInForAdminUseCase.signIn(req)

            // then
            verify(exactly = 1) { userService.readByNickname(nickname) }
            assertEquals(1L, result.id)
        }

        @Test
        fun `실패 - 사용자가 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val nickname = "no-user"
            val request = AdminSignInReq(nickname)

            every { userService.readByNickname(nickname) } returns null

            // when
            val exception = assertThrows(UserException::class.java) {
                signInForAdminUseCase.signIn(request)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { userService.readByNickname(nickname) }
        }

        @Test
        fun `실패 - ADMIN 권한이 아니면 FORBIDDEN 예외를 던진다`() {
            // given
            val nickname = "normal"
            val request = AdminSignInReq(nickname = nickname)

            val user = User(
                nickname = nickname,
                role = Role.USER,
                pointDebt = 0L
            )

            every { userService.readByNickname(nickname) } returns user

            // when
            val exception = assertThrows(AdminAuthException::class.java) {
                signInForAdminUseCase.signIn(request)
            }

            // then
            assertEquals(AdminAuthErrorType.FORBIDDEN, exception.errorType)

            verify(exactly = 1) { userService.readByNickname(nickname) }
        }
    }
}