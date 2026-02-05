package kr.co.rouletteup.common.response.success

import com.fasterxml.jackson.annotation.JsonInclude
import kr.co.rouletteup.common.response.StatusCode
import kr.co.rouletteup.common.response.success.type.SuccessType

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SuccessResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null,
) {
    companion object {
        fun ok(): SuccessResponse<Unit> =
            SuccessResponse(
                code = StatusCode.OK.code,
                message = "요청이 성공하였습니다.",
            )

        fun from(success: SuccessType): SuccessResponse<Unit> =
            SuccessResponse(
                code = success.status.code,
                message = success.message,
            )

        fun <T> from(data: T): SuccessResponse<T> =
            SuccessResponse(
                code = StatusCode.OK.code,
                message = "요청이 성공하였습니다.",
                data = data,
            )

        fun <T> of(success: SuccessType, data: T): SuccessResponse<T> =
            SuccessResponse(
                code = success.status.code,
                message = success.message,
                data = data,
            )
    }
}
