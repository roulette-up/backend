package kr.co.rouletteup.admin.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kr.co.rouletteup.admin.auth.dto.AdminSignInReq
import kr.co.rouletteup.admin.auth.dto.AdminSignInRes
import kr.co.rouletteup.admin.auth.usecase.SignInForAdminUseCase
import kr.co.rouletteup.common.auth.AdminAuthInterceptor
import kr.co.rouletteup.exception.AdminAuthErrorType
import kr.co.rouletteup.exception.AdminAuthException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath

@WebMvcTest(AdminAuthController::class)
class AdminAuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var signInForAdminUseCase: SignInForAdminUseCase

    @MockitoBean
    private lateinit var adminAuthInterceptor: AdminAuthInterceptor

    @BeforeEach
    fun setUp() {
        given(adminAuthInterceptor.preHandle(any(), any(), any())).willReturn(true)
    }

    @Nested
    @DisplayName("어드민 로그인 API - POST /api/v1/admin/auth/sign-in")
    inner class SignIn {

        @Test
        fun `성공 - ADMIN 유저면 로그인 응답을 반환한다`() {
            // given
            val request = AdminSignInReq(nickname = "admin")

            val response = AdminSignInRes(
                id = 1L
            )

            given(signInForAdminUseCase.signIn(any()))
                .willReturn(response)

            // when
            val resultActions = mockMvc.post("/api/v1/admin/auth/sign-in") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.code").value(200)
                    jsonPath("$.message").value("요청이 성공하였습니다.")
                    jsonPath("$.data.id").value(1)
                    jsonPath("$.data.nickname").value("admin")
                }
        }

        @Test
        fun `실패 - ADMIN 권한이 아니면 예외`() {
            // given
            val request = AdminSignInReq(nickname = "user")

            given(signInForAdminUseCase.signIn(any()))
                .willThrow(AdminAuthException(AdminAuthErrorType.FORBIDDEN))

            // when
            val resultActions = mockMvc.post("/api/v1/admin/auth/sign-in") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isForbidden() }
                    jsonPath("$.code").value("AD001")
                    jsonPath("$.message").value("관리자 권한이 필요합니다.")
                }
        }
    }
}
