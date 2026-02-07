package kr.co.rouletteup.domain.point.service

import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.repository.PointRecordRepository
import kr.co.rouletteup.domain.point.type.PointStatus
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

    fun readAllByUserIdAndStatusOrderByExpiresAtAsc(userId: Long, status: PointStatus): List<PointRecord> =
        pointRecordRepository.findAllByUserIdAndStatusOrderByExpiresAtAsc(userId, status)

    fun readAllByUserId(userId: Long, pageable: Pageable): Page<PointRecord> =
        pointRecordRepository.findAllByUserId(userId, pageable)

    fun readExpiringOn(expiresAt: LocalDate): List<PointRecord> =
        pointRecordRepository.findExpiringOn(expiresAt)

    fun readAllByUserIdAndIds(userId: Long, ids: List<Long>): List<PointRecord> {
        return pointRecordRepository.findAllByUserIdAndIdIn(userId, ids)
    }

    fun existsByUserIdAndRouletteDate(userId: Long, rouletteDate: LocalDate): Boolean =
        pointRecordRepository.existsByUserIdAndRouletteDate(userId, rouletteDate)

    fun updateStatusToExpiredByDate(expiresAt: LocalDate) {
        pointRecordRepository.updateStatusToExpiredByDate(expiresAt)
    }
}
