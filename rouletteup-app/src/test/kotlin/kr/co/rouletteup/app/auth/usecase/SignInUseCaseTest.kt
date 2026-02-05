package kr.co.rouletteup.app.auth.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertEquals
import kr.co.rouletteup.app.auth.dto.SignInReq
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.service.UserService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SignInUseCaseTest {

    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var signInUseCase: SignInUseCase

    @Nested
    @DisplayName("로그인")
    inner class SignIn {

        @Test
        fun `이미 존재하는 사용자는 회원가입을 진행하지 않는다` () {
            // given
            val request = mockk<SignInReq>(relaxed = true)
            val user = mockk<User>(relaxed = true)

            every { userService.readByNickname(request.nickname) } returns user

            // when
            val result = signInUseCase.signIn(request)

            // then
            assertEquals(user.id, result.id)
            verify(exactly = 1) { userService.readByNickname(request.nickname) }
            verify(exactly = 0) { userService.save(request.toEntity()) }
        }

        @Test
        fun `존재하지 않는 사용자는 회원가입을 진행한다`() {
            // given
            val request = mockk<SignInReq>(relaxed = true)
            val user = mockk<User>(relaxed = true)

            every { userService.readByNickname(request.nickname) } returns null
            every { userService.save(request.toEntity()) } returns user

            // when
            val result = signInUseCase.signIn(request)

            // then
            assertEquals(user.id, result.id)
            verify(exactly = 1) { userService.readByNickname(request.nickname) }
            verify(exactly = 1) { userService.save(request.toEntity()) }
        }
    }
}
