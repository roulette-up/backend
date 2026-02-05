package kr.co.rouletteup.domain.point.type

enum class PointStatus {
    AVAILABLE,   // 사용 가능
    USED,        // 전액 사용됨
    EXPIRED,     // 만료됨
    CANCELED     // 취소됨(어드민)
}
