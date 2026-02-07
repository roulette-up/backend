package kr.co.rouletteup.domain.point.type

enum class DebtLedgerStatus {
    APPLIED,    // 유효한 이력
    REVERSED,   // 되돌림 처리된 이력
    IGNORED     // 무시 (해당 부채 삭감 포인트도 회수)
}
