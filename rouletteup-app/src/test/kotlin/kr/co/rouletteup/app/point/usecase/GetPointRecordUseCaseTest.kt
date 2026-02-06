package kr.co.rouletteup.app.point.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import java.time.LocalDate
import kotlin.test.Test
import kr.co.rouletteup.domain.common.entity.BaseEntity
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.point.type.PointStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockKExtension::class)
class GetPointRecordUseCaseTest {

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @InjectMockKs
    private lateinit var getPointRecordUseCase: GetPointRecordUseCase

    @Nested
    @DisplayName("포인트 내역 조회")
    inner class GetMyRecord {

        @Test
        fun `포인트 기록을 조회하면 Page로 매핑하여 반환한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 2)

            val record1 = PointRecord(
                grantedPoint = 500,
                remainingPoint = 500,
                status = PointStatus.AVAILABLE,
                expiresAt = LocalDate.of(2026, 3, 8),
                userId = userId,
                rouletteDate = LocalDate.of(2026, 2, 6)
            ).apply { setId(1L) }

            val record2 = PointRecord(
                grantedPoint = 300,
                remainingPoint = 100,
                status = PointStatus.AVAILABLE,
                expiresAt = LocalDate.of(2026, 3, 9),
                userId = userId,
                rouletteDate = LocalDate.of(2026, 2, 6)
            ).apply { setId(2L) }

            every { pointRecordService.readAllByUserId(userId, pageable) } returns PageImpl(
                listOf(record1, record2),
                pageable,
                2
            )

            // when
            val result = getPointRecordUseCase.getMyRecords(userId, pageable)

            // then
            assertEquals(2, result.totalElements)
            assertEquals(2, result.content.size)
            assertEquals(pageable.pageNumber, result.pageable.pageNumber)
            assertEquals(pageable.pageSize, result.pageable.pageSize)

            assertEquals(1L, result.content[0].id)
            assertEquals(2L, result.content[1].id)

            verify(exactly = 1) { pointRecordService.readAllByUserId(userId, pageable) }
        }

        private fun PointRecord.setId(id: Long) {
            val field = BaseEntity::class.java.getDeclaredField("id")
            field.isAccessible = true
            field.set(this, id)
        }

        @Test
        fun `기록이 비어있으면 빈 Page를 반환한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 10)

            every { pointRecordService.readAllByUserId(userId, pageable) } returns PageImpl(
                emptyList(),
                pageable,
                0
            )

            // when
            val result = getPointRecordUseCase.getMyRecords(userId, pageable)

            // then
            assertEquals(0, result.totalElements)
            assertEquals(0, result.content.size)

            verify(exactly = 1) { pointRecordService.readAllByUserId(userId, pageable) }
        }
    }
}
