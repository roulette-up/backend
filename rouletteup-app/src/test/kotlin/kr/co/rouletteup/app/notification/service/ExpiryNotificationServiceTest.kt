package kr.co.rouletteup.app.notification.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.domain.notification.entity.Notification
import kr.co.rouletteup.domain.notification.service.NotificationService
import kr.co.rouletteup.domain.notification.type.ExpiryNoticeType
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class ExpiryNotificationServiceTest {

    @MockK
    private lateinit var notificationService: NotificationService

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @InjectMockKs
    private lateinit var expiryNotificationService: ExpiryNotificationService

    @Test
    fun `금일 기준 D7, D3 만료 예정 포인트를 조회해 알림을 생성하고 저장한다`() {
        // given
        val today = LocalDate.now()

        val d7 = today.plusDays(ExpiryNoticeType.D7.daysBefore)
        val d3 = today.plusDays(ExpiryNoticeType.D3.daysBefore)

        val pointRecord1 = mockk<PointRecord> {
            every { userId } returns 1L
            every { expiresAt } returns d7
            every { remainingPoint } returns 1200L
        }
        val pointRecord2 = mockk<PointRecord> {
            every { userId } returns 2L
            every { expiresAt } returns d7
            every { remainingPoint } returns 300L
        }
        val pointRecord3 = mockk<PointRecord> {
            every { userId } returns 3L
            every { expiresAt } returns d3
            every { remainingPoint } returns 900L
        }

        every { pointRecordService.readExpiringOn(d7) } returns listOf(pointRecord1, pointRecord2)
        every { pointRecordService.readExpiringOn(d3) } returns listOf(pointRecord3)

        val slot = slot<List<Notification>>()
        every { notificationService.saveAll(capture(slot)) } just Runs

        // when
        expiryNotificationService.generateExpiryNoticesForToday()

        // then
        verify(exactly = 1) { pointRecordService.readExpiringOn(d7) }
        verify(exactly = 1) { pointRecordService.readExpiringOn(d3) }
        verify(exactly = 1) { notificationService.saveAll(any()) }

        val saved = slot.captured
        assertEquals(3, saved.size)

        // D7 2개, D3 1개 생성 확인
        assertEquals(2, saved.count { it.type == ExpiryNoticeType.D7 })
        assertEquals(1, saved.count { it.type == ExpiryNoticeType.D3 })

        // 내용 매핑 확인 (userId / expiresAt / expiringPoint)
        val n1 = saved.first { it.userId == 1L }
        assertEquals(ExpiryNoticeType.D7, n1.type)
        assertEquals(d7, n1.expiresAt)
        assertEquals(1200L, n1.expiringPoint)

        val n2 = saved.first { it.userId == 2L }
        assertEquals(ExpiryNoticeType.D7, n2.type)
        assertEquals(d7, n2.expiresAt)
        assertEquals(300L, n2.expiringPoint)

        val n3 = saved.first { it.userId == 3L }
        assertEquals(ExpiryNoticeType.D3, n3.type)
        assertEquals(d3, n3.expiresAt)
        assertEquals(900L, n3.expiringPoint)
    }
}
