package kr.co.rouletteup.app.point.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PointExpiryServiceTest {

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @InjectMockKs
    private lateinit var pointExpiryService: PointExpiryService

    @Test
    fun `오늘 날짜 기준으로 포인트 상태를 EXPIRED로 업데이트한다`() {
        // given
        val today = LocalDate.now()
        every { pointRecordService.updateStatusToExpiredByDate(today) } just Runs

        // when
        pointExpiryService.expireTodayPoints()

        // then
        verify(exactly = 1) { pointRecordService.updateStatusToExpiredByDate(today) }
    }
}
