package kr.co.rouletteup.common.response.error.type

import kr.co.rouletteup.common.response.StatusCode

interface BaseErrorType {
    val status: StatusCode
    val code: String
    val message: String
}
