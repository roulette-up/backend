package kr.co.rouletteup.admin.user.controller

import java.time.LocalDateTime
import kr.co.rouletteup.admin.user.dto.AdminUserDetail
import kr.co.rouletteup.admin.user.dto.AdminUserSummary
import kr.co.rouletteup.admin.user.usecase.GetUserForAdminUseCase
import kr.co.rouletteup.domain.user.type.Role
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminUserController::class)
class AdminUserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var getUserForAdminUseCase: GetUserForAdminUseCase

    @Nested
    @DisplayName("전체 사용자 조회 API")
    inner class GetUsers {

        @Test
        fun `페이징으로 soft delete 포함 사용자 목록 조회`() {
            // given
            val pageable = PageRequest.of(0, 20)

            val response = AdminUserSummary(
                id = 1L,
                nickname = "tester",
                deletedAt = null,
            )

            val page = PageImpl(listOf(response), pageable, 1)

            given(getUserForAdminUseCase.getUsers(pageable))
                .willReturn(page)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/users") {
                param("page", "0")
                param("size", "20")
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.content[0].id").value(response.id)
                    jsonPath("$.data.content[0].nickname").value(response.nickname)
                }
        }
    }

    @Nested
    @DisplayName("특정 사용자 조회 API")
    inner class GetUserById {

        @Test
        fun `id로 soft delete 포함 사용자 단건 조회`() {
            // given
            val userId = 1L

            val response = AdminUserDetail(
                id = userId,
                nickname = "tester",
                pointDebt = 100L,
                deletedAt = null,
                createdAt = LocalDateTime.now(),
                role = Role.USER
            )

            given(getUserForAdminUseCase.getUserById(userId))
                .willReturn(response)

            // when
            val resultActions = mockMvc.get("/api/v1/admin/users/$userId")

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.id").value(response.id)
                    jsonPath("$.data.nickname").value(response.nickname)
                    jsonPath("$.data.pointDebt").value(response.pointDebt)
                }
        }
    }
}
