package kr.co.rouletteup.domain.point.repository

import java.time.LocalDate
import kr.co.rouletteup.domain.point.entity.PointRecord
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PointRecordRepository : JpaRepository<PointRecord, Long> {
    fun existsByUserIdAndRouletteDate(userId: Long, rouletteDate: LocalDate): Boolean
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<PointRecord>
}
