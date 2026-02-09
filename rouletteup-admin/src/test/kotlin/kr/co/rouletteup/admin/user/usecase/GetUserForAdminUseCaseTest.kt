package kr.co.rouletteup.admin.user.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kotlin.test.assertEquals
import kr.co.rouletteup.admin.user.dto.AdminUserDetail
import kr.co.rouletteup.admin.user.dto.AdminUserSummary
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
class GetUserForAdminUseCaseTest {

    @MockK
    private lateinit var userService: UserService

    @InjectMockKs
    private lateinit var getUserForAdminUseCase: GetUserForAdminUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(AdminUserSummary.Companion)
        mockkObject(AdminUserDetail.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(AdminUserSummary.Companion)
        unmockkObject(AdminUserDetail.Companion)
    }

    @Nested
    @DisplayName("전체 사용자 조회")
    inner class GetUsers {

        @Test
        fun `사용자 목록을 페이징 조회하고 AdminUserSummary로 변환한다`() {
            // given
            val pageable: Pageable = PageRequest.of(0, 20)

            val user1 = mockk<User>(relaxed = true)
            val user2 = mockk<User>(relaxed = true)
            val page = PageImpl(listOf(user1, user2), pageable, 2)

            val dto1 = mockk<AdminUserSummary>(relaxed = true)
            val dto2 = mockk<AdminUserSummary>(relaxed = true)

            every { userService.readAll(pageable) } returns page
            every { AdminUserSummary.from(user1) } returns dto1
            every { AdminUserSummary.from(user2) } returns dto2

            // when
            val result = getUserForAdminUseCase.getUsers(pageable)

            // then
            assertThat(result.content).hasSize(2)
            assertThat(result.content[0]).isSameAs(dto1)
            assertThat(result.content[1]).isSameAs(dto2)

            verify(exactly = 1) { userService.readAll(pageable) }
            verify(exactly = 1) { AdminUserSummary.from(user1) }
            verify(exactly = 1) { AdminUserSummary.from(user2) }
        }
    }

    @Nested
    @DisplayName("특정 사용자 조회")
    inner class GetUserById {

        @Test
        fun `사용자가 존재하면 AdminUserDetail로 변환해 반환한다`() {
            // given
            val userId = 1L
            val user = mockk<User>(relaxed = true)
            val expected = mockk<AdminUserDetail>(relaxed = true)

            every { userService.readById(userId) } returns user
            every { AdminUserDetail.from(user) } returns expected

            // when
            val result = getUserForAdminUseCase.getUserById(userId)

            // then
            assertEquals(result, result)

            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 1) { AdminUserDetail.from(user) }
        }

        @Test
        fun `사용자가 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val userId = 999L
            every { userService.readById(userId) } returns null

            // when
            val exception = assertThrows<UserException> {
                getUserForAdminUseCase.getUserById(userId)
            }

            // then
            assertEquals(UserErrorType.NOT_FOUND, exception.errorType)

            verify(exactly = 1) { userService.readById(userId) }
            verify(exactly = 0) { AdminUserDetail.from(any()) }
        }
    }
}
