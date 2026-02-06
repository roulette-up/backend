package kr.co.rouletteup.app.roulette.usercase

import java.time.LocalDate
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kr.co.rouletteup.app.roulette.dto.RouletteParticipateRes
import kr.co.rouletteup.app.roulette.service.RouletteParticipationService
import kr.co.rouletteup.common.response.error.type.GlobalErrorType
import kr.co.rouletteup.common.response.exception.GlobalException
import kr.co.rouletteup.common.util.RouletteRandomUtil
import kr.co.rouletteup.domain.point.exception.PointErrorType
import kr.co.rouletteup.domain.point.exception.PointException
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import kr.co.rouletteup.infrastructure.cache.constant.CacheNames
import kr.co.rouletteup.infrastructure.cache.repository.CacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ParticipateRouletteUseCase(
    private val dailyRouletteService: DailyRouletteService,
    private val pointRecordService: PointRecordService,
    private val rouletteParticipationService: RouletteParticipationService,
    private val cacheRepository: CacheRepository,
) {

    private val log = LoggerFactory.getLogger(ParticipateRouletteUseCase::class.java)

    // ReentrantLock에 true설정으로 진입시점 공정성 보장
    private val lock = ReentrantLock(true)

    // 평균 락 대기 시간 250ms
    private val LOCK_WAIT_SECONDS = 3L

    /**
     * 룰렛 참여 진입점
     *
     * @param userId 사용자 ID(PK)
     * @return 당첨된 포인트 DTO
     */
    fun participate(userId: Long): RouletteParticipateRes {
        val today = LocalDate.now()

        // 1일 1회 검증
        if (pointRecordService.existsByUserIdAndRouletteDate(userId, today)) {
            throw PointException(PointErrorType.ALREADY_PARTICIPATED)
        }

        val acquired = lock.tryLock(LOCK_WAIT_SECONDS, TimeUnit.SECONDS)
        if (!acquired) {
            log.warn("[Roulette] 룰렛 참여 락 획득 실패 userId={}, date={}", userId, today)
            throw GlobalException(GlobalErrorType.TOO_MANY_REQUESTS)
        }

        try {
            return RouletteParticipateRes.of(participateWithLock(userId, today))
        } finally {
            lock.unlock()
        }

    }

    /**
     * 룰렛 참여 처리 시작 메서드
     * - Lock을 통해 임계 구역을 설정하여 순차적으로 처리
     * - Lock은 x초까지 대기. (한번의 요청이 xxxms. 최대 100번 룰렛 가능하니 그 이상 시간 대기 가능)
     *
     * <처리 순서>
     * 1. 락 획득
     * 2. 총 예산 및 사용 예산 캐시 조회 (없으면 DB 조회)
     * 3. 잔여 포인트가 100 포인트 미만이면 룰렛 예산 소진 처리
     * 4. 포인트 랜덤 생성
     * 5. 룰렛 정보 업데이트 및 참여 기록 처리 (여기서 트랜잭션을 시도하여 DB 커넥션 점유 최소화)
     * 6. 캐시 갱신
     * 7. 락 반납
     *
     * @param userId 사용자 ID(PK)
     * @param date 룰렛 참여 날짜
     * @return 당첨된 포인트
     */
    private fun participateWithLock(userId: Long, date: LocalDate): Long {
        // 총 예산 및 사용 예산 캐시 조회 (없으면 DB 조회)
        val (total, used) = loadBudgetFromCacheOrDb(date)

        // 잔여 포인트가 100 미만이면, 룰렛 예산 소진 처리 (100~1000p 발급 받기에)
        val remaining = total - used
        if (remaining < 100) {
            throw RouletteException(RouletteErrorType.BUDGET_EXHAUSTED)
        }

        // 잔여 포인트 기준 포인트 랜덤 생성
        val reward = RouletteRandomUtil.generateReward(remaining)

        // 룰렛 정보 업데이트 및 참여 기록 트랜잭션 처리
        rouletteParticipationService.participateAndRecordPoint(
            userId, date, reward
        )

        // 캐시 갱신
        cacheRepository.put(
            CacheNames.USED_BUDGET, date.toString(), used + reward
        )

        return reward
    }

    /**
     * 총 예산 및 사용 예산 조회
     * - 캐시 조회하여 존재하면 반환
     * - 존재하지 않으면 DB 조회 후 반환
     * - DB 조회 후, 총 예산만 캐시 저장 (사용 예산은 룰렛 처리 과정에서 변동 가능)
     *
     * @param date 룰렛 참여 날짜
     * @return (총 예산, 사용 예산)
     */
    private fun loadBudgetFromCacheOrDb(date: LocalDate): Pair<Long, Long> {
        val key = date.toString()
        val total = cacheRepository.get(CacheNames.TOTAL_BUDGET, key, Long::class)
        val used = cacheRepository.get(CacheNames.USED_BUDGET, key, Long::class)

        // 둘 다 있으면 바로 반환
        if (total != null && used != null) {
            return total to used
        }

        // 캐시 미스 → DB 조회 후 반환
        val dailyRoulette = dailyRouletteService.readByRouletteDate(date)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        // 총 예산만 캐시 저장 (사용 예산은 룰렛 처리 성공 후 진행)
        cacheRepository.put(CacheNames.TOTAL_BUDGET, date.toString(), dailyRoulette.totalBudget)

        return dailyRoulette.totalBudget to dailyRoulette.usedBudget
    }
}
