package kr.co.rouletteup.domain.point.repository

import java.time.LocalDate
import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.point.entity.PointRecord
import kr.co.rouletteup.domain.point.type.PointStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PointRecordRepository : JpaRepository<PointRecord, Long> {
    fun existsByUserIdAndRouletteDate(userId: Long, rouletteDate: LocalDate): Boolean
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<PointRecord>
    fun findAllByUserIdAndStatusOrderByExpiresAtAsc(userId: Long, status: PointStatus): List<PointRecord>
    fun findAllByUserIdAndIdIn(userId: Long, ids: List<Long>): List<PointRecord>

    @Query(
        value = """
        SELECT *
        FROM point_record
        WHERE expires_at = :expiresAt
            AND status = 'AVAILABLE'
            AND remaining_point > 0
    """, nativeQuery = true
    )
    fun findExpiringOn(@Param("expiresAt") expiresAt: LocalDate): List<PointRecord>

    @Modifying
    @Query(
        value = """
        UPDATE point_record
        SET status = 'EXPIRED'
        WHERE expires_at = :expiresAt
            AND status = 'AVAILABLE'
    """, nativeQuery = true
    )
    fun updateStatusToExpiredByDate(@Param("expiresAt") expiresAt: LocalDate)

    @Query(
        value = """
            SELECT *
            FROM point_record
            WHERE user_id = :userId
            ORDER BY id DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM point_record
            WHERE user_id = :userId
        """, nativeQuery = true
    )
    fun findAllByUserIdIdIncludeDeleted(
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<PointRecord>

    @Query(
        value = """
            SELECT *
            FROM point_record
            WHERE roulette_date = :date
            ORDER BY id DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM point_record
            WHERE roulette_date = :date
        """, nativeQuery = true
    )
    fun findAllByRouletteDateIncludeDeleted(
        @Param("date") date: LocalDate,
        pageable: Pageable
    ): Page<PointRecord>

}
