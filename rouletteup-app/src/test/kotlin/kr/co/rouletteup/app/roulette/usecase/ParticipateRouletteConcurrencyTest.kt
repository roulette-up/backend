package kr.co.rouletteup.app.roulette.usecase

import java.time.LocalDate
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kr.co.rouletteup.app.roulette.usercase.ParticipateRouletteUseCase
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.repository.PointRecordRepository
import kr.co.rouletteup.domain.roulette.repository.DailyRouletteRepository
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.repository.UserRepository
import kr.co.rouletteup.domain.user.type.Role
import kr.co.rouletteup.infrastructure.cache.constant.CacheNames
import kr.co.rouletteup.infrastructure.cache.repository.CacheRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.support.TransactionTemplate

@SpringBootTest
@ActiveProfiles("test")
class ParticipateRouletteConcurrencyTest{

    @Autowired
    lateinit var participateRouletteUseCase: ParticipateRouletteUseCase

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var dailyRouletteRepository: DailyRouletteRepository

    @Autowired
    lateinit var pointRecordRepository: PointRecordRepository

    @Autowired
    lateinit var cacheRepository: CacheRepository

    @Autowired
    lateinit var txTemplate: TransactionTemplate

    private val today: LocalDate = LocalDate.now()

    @BeforeEach
    fun setup() {
        // 유저 100명 생성
        txTemplate.executeWithoutResult {
            val users = (1L..100L).map { id ->
                User(
                    nickname = "user-$id",
                    role = Role.USER
                )
            }
            userRepository.saveAll(users)
        }
    }

    @Test
    fun `100명이 동시에 participate 요청 시 정합성이 맞아야 한다`() {
        // given
        val threadCount = 100
        val pool = Executors.newFixedThreadPool(32)

        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)

        val userIds = txTemplate.execute {
            userRepository.findAll().map { it.id!! }
        }!!

        // when
        userIds.forEach { userId ->
            pool.submit {
                try {
                    startLatch.await()
                    participateRouletteUseCase.participate(userId)
                } finally {
                    doneLatch.countDown()
                }
            }
        }

        // 동시에 시작
        startLatch.countDown()

        // 모두 종료 대기
        val finished = doneLatch.await(30, TimeUnit.SECONDS)
        pool.shutdown()

        if (!finished) {
            throw IllegalStateException("동시성 테스트가 시간 내에 종료되지 않았습니다.")
        }

        // then
        // 전체 사용자가 획득한 포인트 합 (User Entity의 availablePoint)
        val sumAvailablePoint = txTemplate.execute {
            userRepository.findAll().sumOf(User::availablePoint)
        }!!

        // 전체 사용자가 획득한 포인트 레코드 합
        val sumGranted = txTemplate.execute {
            pointRecordRepository.findAll().sumOf(PointRecord::grantedPoint)
        }!!

        // 금일 룰렛의 사용 예산 (사용자가 당첨된 포인트 합)
        val usedBudget = txTemplate.execute {
            dailyRouletteRepository.findByRouletteDate(today)!!.usedBudget
        }!!

        // 총 사용된 예산 캐시
        val cachedUsedBudget =
            cacheRepository.get(CacheNames.USED_BUDGET, today.toString(), Long::class)

        assertEquals(sumAvailablePoint, sumGranted)
        assertEquals(sumGranted, usedBudget)
        assertEquals(usedBudget, cachedUsedBudget)

        // 참여자수/레코드 수 확인까지 하고 싶으면:
        val recordCount = txTemplate.execute {
            pointRecordRepository.count()
        }!!
        assertEquals(100L, recordCount)
    }
}
