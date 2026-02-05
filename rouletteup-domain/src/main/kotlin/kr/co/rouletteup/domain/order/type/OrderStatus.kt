package kr.co.rouletteup.domain.order.type

enum class OrderStatus {
    COMPLETED,          // 주문 처리 완료
    USER_CANCELLED,     // 사용자 취소
    ADMIN_CANCELLED     // 어드민 취소
}