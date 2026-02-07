package kr.co.rouletteup.app.order.usecase

import java.time.LocalDate
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kr.co.rouletteup.app.order.dto.OrderReq
import kr.co.rouletteup.domain.order.repository.OrderPointUsageRepository
import kr.co.rouletteup.domain.order.repository.OrderRepository
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.repository.PointRecordRepository
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.repository.ProductRepository
import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.repository.UserRepository
import kr.co.rouletteup.domain.user.type.Role
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PurchaseProductConcurrencyTest {

    @Autowired
    private lateinit var orderRepository: OrderRepository

    @Autowired
    private lateinit var productRepository: ProductRepository

    @Autowired
    private lateinit var pointRepository: PointRecordRepository

    @Autowired
    private lateinit var orderPointUsageRepository: OrderPointUsageRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var purchaseProductUseCase: PurchaseProductUseCase

    @BeforeEach
    fun setUp() {
        orderRepository.deleteAll()
        productRepository.deleteAll()
        pointRepository.deleteAll()
        orderPointUsageRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `100명이 동시에 1개씩 구매하면 재고 100개가 정확히 소진되고 주문 100건이 생성된다`() {
        // given
        val price = 1L
        val stock = 100
        val requestQuantity = 1
        val threadCount = 100

        // 상품 저장
        val product = productRepository.save(
            Product(
                name = "상품",
                price = price,
                stockQuantity = stock,
            )
        )


        // 사용자 생성
        val users = (1..threadCount).map { idx ->
            val runId = UUID.randomUUID().toString().substring(0, 8)
            val nickname = "u$idx-$runId"
            userRepository.save(
                User(
                    nickname = nickname,
                    role = Role.USER
                )
            )
        }

        // 각 사용자 포인트 1000 (AVAILABLE, remainingPoint=1000)
        val today = LocalDate.of(2026, 2, 7)
        users.forEach { user ->
            pointRepository.save(
                PointRecord(
                    grantedPoint = 1000L,
                    remainingPoint = 1000L,
                    status = PointStatus.AVAILABLE,
                    expiresAt = today.plusDays(30),
                    userId = user.id!!,
                    rouletteDate = today
                )
            )
        }

        val orderReq = OrderReq(
            productId = product.id!!,
            price = price,
            quantity = requestQuantity
        )

        val startLatch = CountDownLatch(1)
        val doneLatch = CountDownLatch(threadCount)
        val pool = Executors.newFixedThreadPool(32)

        val success = AtomicInteger(0)
        val fail = AtomicInteger(0)

        // when
        users.forEach { user ->
            pool.submit {
                try {
                    startLatch.await()
                    purchaseProductUseCase.purchaseProduct(user.id!!, orderReq)
                    success.incrementAndGet()
                } catch (e: Exception) {
                    fail.incrementAndGet()
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
        // 정확히 100명 성공 확인
        assertEquals(100, success.get())
        assertEquals(0, fail.get())

        val updatedProduct = productRepository.findById(product.id!!).orElseThrow()
        assertEquals(0, updatedProduct.stockQuantity)

        // 주문 100건
        assertEquals(100, orderRepository.count())

        // 사용내역 100건
        assertEquals(100, orderPointUsageRepository.count())
    }
}
