package kr.co.rouletteup.domain.point.service

import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.repository.PointRecordRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PointRecordService(
    private val pointRecordRepository: PointRecordRepository,
) {

    fun save(pointRecord: PointRecord) {
        pointRecordRepository.save(pointRecord)
    }

    fun readAllByUserId(userId: Long, pageable: Pageable): Page<PointRecord> =
        pointRecordRepository.findAllByUserId(userId, pageable)

    fun existsByUserIdAndRouletteDate(userId: Long, rouletteDate: LocalDate): Boolean =
        pointRecordRepository.existsByUserIdAndRouletteDate(userId, rouletteDate)
}
