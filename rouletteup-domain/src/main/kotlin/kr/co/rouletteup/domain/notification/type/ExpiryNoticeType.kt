package kr.co.rouletteup.domain.notification.type

enum class ExpiryNoticeType(val daysBefore: Long) {
    D7(7),      // 포인트 만료 7일 전
    D3(3)       // 포인트 만료 3일 전
}
