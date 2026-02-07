package kr.co.rouletteup.admin.roulette.usecase

import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteRes
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockKExtension::class)
class GetRouletteForAdminUseCaseTest {

    @MockK
    private lateinit var dailyRouletteService: DailyRouletteService

    @InjectMockKs
    private lateinit var getRouletteForAdminUseCase: GetRouletteForAdminUseCase

    @BeforeEach
    fun setUp() {
        mockkObject(AdminRouletteRes.Companion)
    }

    @AfterEach
    fun tearDown() {
        unmockkObject(AdminRouletteRes.Companion)
    }

    @Test
    fun `전체 룰렛을 soft delete 포함 페이징 조회하고 AdminRouletteRes로 변환한다`() {
        // given
        val pageable: Pageable = PageRequest.of(0, 20)

        val roulette1 = mockk<DailyRoulette>(relaxed = true)
        val roulette2 = mockk<DailyRoulette>(relaxed = true)

        val page = PageImpl(listOf(roulette1, roulette2), pageable, 2)

        val dto1 = mockk<AdminRouletteRes>(relaxed = true)
        val dto2 = mockk<AdminRouletteRes>(relaxed = true)

        every { dailyRouletteService.readAllIncludeDeleted(pageable) } returns page
        every { AdminRouletteRes.form(roulette1) } returns dto1
        every { AdminRouletteRes.form(roulette2) } returns dto2

        // when
        val result = getRouletteForAdminUseCase.getRoulettes(pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0]).isSameAs(dto1)
        assertThat(result.content[1]).isSameAs(dto2)

        verify(exactly = 1) { dailyRouletteService.readAllIncludeDeleted(pageable) }
        verify(exactly = 1) { AdminRouletteRes.form(roulette1) }
        verify(exactly = 1) { AdminRouletteRes.form(roulette2) }
    }
}
