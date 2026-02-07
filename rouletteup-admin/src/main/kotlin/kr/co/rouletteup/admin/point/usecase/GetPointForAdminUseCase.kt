package kr.co.rouletteup.admin.point.usecase

import java.time.LocalDate
import kr.co.rouletteup.admin.point.dto.AdminPointRes
import kr.co.rouletteup.domain.point.service.PointRecordService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPointForAdminUseCase(
    private val pointRecordService: PointRecordService,
) {

    /**
     * 사용자를 통한 포인트 내역 조회 메서드 (soft delete 포함)
     *
     * @param userId 사용자 ID(PK)
     * @param pageable 페이지 크기
     * @return 포인트 내역 페이징 DTO
     */
    @Transactional(readOnly = true)
    fun getPointRecordByUserId(userId: Long, pageable: Pageable): Page<AdminPointRes> =
        pointRecordService.readAllByUserIdIdIncludeDeleted(userId, pageable)
            .map { point -> AdminPointRes.from(point) }

    /**
     * 룰렛 날짜를 통한 포인트 내역 조회 메서드 (soft delete 포함)
     *
     * @param rouletteDate 룰렛 날짜
     * @param pageable 페이지 크기
     * @return 포인트 내역 페이징 DTO
     */
    @Transactional(readOnly = true)
    fun getPointRecordByRouletteDate(rouletteDate: LocalDate, pageable: Pageable): Page<AdminPointRes> =
        pointRecordService.readAllByRouletteDateIncludeDeleted(rouletteDate, pageable)
            .map { point -> AdminPointRes.from(point) }

}
