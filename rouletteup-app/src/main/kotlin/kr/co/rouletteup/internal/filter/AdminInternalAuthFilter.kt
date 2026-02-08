package kr.co.rouletteup.internal.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kr.co.rouletteup.internal.properties.AdminInternalAuthProperties
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * 어드민, 앱 내부 통신에 대해서 검증하기 위한 필터
 */
@Component
class AdminInternalAuthFilter(
    private val props: AdminInternalAuthProperties,
) : OncePerRequestFilter() {

    /**
     * 외부 요청은 넘기기
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return !request.requestURI.startsWith("/internal/v1/admin/")
    }

    /**
     * 토큰 헤더를 통해 설정된 토큰이 맞는지 검증
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val token = request.getHeader(props.tokenHeaderName)
        if (token.isNullOrBlank() || token != props.token) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            return
        }
        filterChain.doFilter(request, response)
    }
}
