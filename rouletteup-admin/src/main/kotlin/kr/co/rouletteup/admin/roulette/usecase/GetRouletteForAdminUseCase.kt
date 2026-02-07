package kr.co.rouletteup.admin.roulette.usecase

import java.time.LocalDate
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteRes
import kr.co.rouletteup.domain.roulette.exception.RouletteErrorType
import kr.co.rouletteup.domain.roulette.exception.RouletteException
import kr.co.rouletteup.domain.roulette.service.DailyRouletteService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetRouletteForAdminUseCase(
    private val dailyRouletteService: DailyRouletteService,
) {

    /**
     * 전체 룰렛 조회 메서드 (soft delete 포함)
     *
     * @param pageable 페이지 크기
     * @return 룰렛 정보 페이징 DTO
     */
    @Transactional(readOnly = true)
    fun getRoulettes(pageable: Pageable): Page<AdminRouletteRes> =
        dailyRouletteService.readAllIncludeDeleted(pageable)
            .map { roulette -> AdminRouletteRes.form(roulette) }

    /**
     * 오늘 진행 중인 룰렛 정보 조회 메서드
     *
     * @return 오늘 룰렛 정보 DTO
     */
    @Transactional(readOnly = true)
    fun getTodayRoulette(): AdminRouletteRes {
        val today = LocalDate.now()

        val roulette = dailyRouletteService.readByRouletteDate(today)
            ?: throw RouletteException(RouletteErrorType.NOT_FOUND)

        return AdminRouletteRes.form(roulette)
    }
}
