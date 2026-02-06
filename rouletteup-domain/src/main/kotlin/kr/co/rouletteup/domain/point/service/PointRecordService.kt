package kr.co.rouletteup.domain.point.service

import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.repository.PointRecordRepository
import org.springframework.stereotype.Service

@Service
class PointRecordService(
    private val pointRecordRepository: PointRecordRepository,
) {

    fun save(pointRecord: PointRecord) {
        pointRecordRepository.save(pointRecord)
    }

    fun existsByUserIdAndRouletteDate(userId: Long, rouletteDate: LocalDate): Boolean =
        pointRecordRepository.existsByUserIdAndRouletteDate(userId, rouletteDate)
}
