package kr.co.rouletteup.common.response.exception

import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.error.ErrorResponse
import kr.co.rouletteup.common.response.error.type.GlobalErrorType
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    /**
     * 비즈니스 로직 예외 처리
     * - BaseException 공통 처리
     */
    @ExceptionHandler(BaseException::class)
    fun handleCustomException(e: BaseException): ResponseEntity<*> {
        val error = e.errorType
        log.warn("[Business Warning] {}", error.message)

        return ResponseEntity
            .status(error.status.code)
            .body(
                ErrorResponse.from(error = error)
            )
    }

    /**
     * 유효성 검증 실패
     * - 필드별 에러 메시지를 key-value 형태로 반환
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<*> {
        val error = GlobalErrorType.VALIDATION_ERROR
        val errors: Map<String, String> =
            e.bindingResult.fieldErrors.associate { fe: FieldError ->
                fe.field to (fe.defaultMessage ?: "Invalid value")
            }

        return ResponseEntity
            .status(StatusCode.BAD_REQUEST.code)
            .body(
                ErrorResponse.from(
                    error = error,
                    errors = errors
                )
            )
    }

    /**
     * 그 외 예외 처리 (서버 에러)
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<*> {
        log.error("[Error Occurred] {}", e.message, e)
        val error = GlobalErrorType.INTERNAL_SERVER_ERROR
        return ResponseEntity
            .status(error.status.code)
            .body(
                ErrorResponse.from(error)
            )
    }
}
