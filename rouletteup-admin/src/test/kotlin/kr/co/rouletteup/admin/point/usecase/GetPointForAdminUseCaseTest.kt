package kr.co.rouletteup.admin.point.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import java.time.LocalDate
import kr.co.rouletteup.admin.point.dto.AdminPointRes
import kr.co.rouletteup.domain.point.dto.PointRecordWithNicknameDto
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockKExtension::class)
class GetPointForAdminUseCaseTest {

    @MockK
    private lateinit var pointRecordService: PointRecordService

    @InjectMockKs
    private lateinit var getPointForAdminUseCase: GetPointForAdminUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(AdminPointRes.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(AdminPointRes.Companion)
    }

    @Nested
    @DisplayName("사용자 포인트 내역 조회")
    inner class GetByUserId {

        @Test
        fun `사용자 id로 포인트 내역을 조회하고 DTO로 변환한다`() {
            // given
            val userId = 1L
            val pageable = PageRequest.of(0, 20)

            val point = mockk<PointRecordWithNicknameDto>(relaxed = true)
            val page = PageImpl(listOf(point), pageable, 1)

            val dto = mockk<AdminPointRes>(relaxed = true)

            every {
                pointRecordService.readAllWithNicknameByUserId(userId, pageable)
            } returns page

            every { AdminPointRes.from(point) } returns dto

            // when
            val result = getPointForAdminUseCase.getPointRecordByUserId(userId, pageable)

            // then
            assertThat(result.content).hasSize(1)
            assertThat(result.content[0]).isSameAs(dto)

            verify(exactly = 1) { pointRecordService.readAllWithNicknameByUserId(userId, pageable) }
            verify(exactly = 1) { AdminPointRes.from(point) }
        }
    }

    @Nested
    @DisplayName("룰렛 날짜별 포인트 내역 조회")
    inner class GetByRouletteDate {

        @Test
        fun `룰렛 날짜로 포인트 내역을 조회하고 DTO로 변환한다`() {
            // given
            val date = LocalDate.of(2026, 2, 7)
            val pageable = PageRequest.of(0, 20)

            val point = mockk<PointRecordWithNicknameDto>(relaxed = true)
            val page = PageImpl(listOf(point), pageable, 1)

            val dto = mockk<AdminPointRes>(relaxed = true)

            every {
                pointRecordService.readAllWithNicknameByRouletteDate(date, pageable)
            } returns page

            every { AdminPointRes.from(point) } returns dto

            // when
            val result = getPointForAdminUseCase.getPointRecordByRouletteDate(date, pageable)

            // then
            assertThat(result.content).hasSize(1)
            assertThat(result.content[0]).isSameAs(dto)

            verify(exactly = 1) { pointRecordService.readAllWithNicknameByRouletteDate(date, pageable) }
            verify(exactly = 1) { AdminPointRes.from(point) }
        }
    }
}
