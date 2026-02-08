package kr.co.rouletteup.app.auth.controller

import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.test.Test
import kr.co.rouletteup.app.auth.dto.SignInReq
import kr.co.rouletteup.app.auth.dto.SignInRes
import kr.co.rouletteup.app.auth.usecase.SignInUseCase
import kr.co.rouletteup.common.response.error.type.GlobalErrorType
import kr.co.rouletteup.internal.filter.AdminInternalAuthFilter
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(
    controllers = [AuthController::class],
    excludeFilters = [
        ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = [AdminInternalAuthFilter::class]
        )
    ]
)
class AuthControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var signInUseCase: SignInUseCase

    @Nested
    @DisplayName("로그인 API")
    inner class SignIn {

        @Test
        fun `로그인을 성공하면 200 응답과 id를 반환한다`() {
            // given
            val request = SignInReq(nickname = "test")
            val response = SignInRes(id = 1L)

            given(signInUseCase.signIn(request))
                .willReturn(response)

            // when
            val resultActions = mockMvc.post("/api/v1/auth/sign-in") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    jsonPath("$.message") { value("요청이 성공하였습니다.") }
                    jsonPath("$.data.id") { value(1) }
                }
        }

        @Test
        fun `닉네임 길이가 2글자 미만이면 400 응답을 반환한다`() {
            // given
            val request = SignInReq(nickname = "a")

            // when
            val resultActions = mockMvc.post("/api/v1/auth/sign-in") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.code") { value(GlobalErrorType.VALIDATION_ERROR.code) }
                    jsonPath("$.message") { value(GlobalErrorType.VALIDATION_ERROR.message) }
                    jsonPath("$.errors.nickname") { value("닉네임은 2~30자여야 합니다.") }
                }
        }

        @Test
        fun `닉네임 길이가 30글자 초과되면 400 응답을 반환한다`() {
            // given
            val request = SignInReq(nickname = "a".repeat(31))

            // when
            val resultActions = mockMvc.post("/api/v1/auth/sign-in") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(request)
            }

            // then
            resultActions
                .andExpect {
                    status { isBadRequest() }
                    jsonPath("$.code") { value(GlobalErrorType.VALIDATION_ERROR.code) }
                    jsonPath("$.message") { value(GlobalErrorType.VALIDATION_ERROR.message) }
                    jsonPath("$.errors.nickname") { value("닉네임은 2~30자여야 합니다.") }
                }
        }
    }
}
