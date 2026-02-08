package kr.co.rouletteup.internal.listener

import kr.co.rouletteup.internal.client.AppCacheEvictClient
import kr.co.rouletteup.internal.event.TodayBudgetChangedEvent
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TodayBudgetChangedEventListener(
    private val appCacheEvictClient: AppCacheEvictClient,
) {
    private val log = LoggerFactory.getLogger(TodayBudgetChangedEventListener::class.java)

    /**
     * 토큰 무효화를 위한 이벤트 처리
     * - 앱 서버에 요청을 보내 캐시 무효화 진행
     * - 트랜잭션이 커밋 이후에 처리
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun on(event: TodayBudgetChangedEvent) {
        runCatching {
            appCacheEvictClient.evictTotalBudget(event.date)
        }.onSuccess {
            log.info("[Admin->App] totalBudget cache evicted. date={}", event.date)
        }.onFailure { e ->
            // 실패해도 DB가 소스라서 치명적 장애로 만들진 않음(로그/모니터링으로 추적)
            log.warn("[Admin->App] cache evict failed. date={}, reason={}", event.date, e.message, e)
        }
    }
}
