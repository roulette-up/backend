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

    @Transactional(readOnly = true)
    fun getPointRecordByUserId(userId: Long, pageable: Pageable): Page<AdminPointRes> =
        pointRecordService.readAllByUserIdIdIncludeDeleted(userId, pageable)
            .map { point -> AdminPointRes.from(point) }

    @Transactional(readOnly = true)
    fun getPointRecordByRouletteDate(rouletteDate: LocalDate, pageable: Pageable): Page<AdminPointRes> =
        pointRecordService.readAllByRouletteDateIncludeDeleted(rouletteDate, pageable)
            .map { point -> AdminPointRes.from(point) }

}
