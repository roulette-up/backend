package kr.co.rouletteup.domain.order.repository

import java.sql.PreparedStatement
import java.sql.Timestamp
import java.time.LocalDateTime
import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import org.springframework.jdbc.core.JdbcTemplate

class CustomOrderPointUsageRepositoryImpl(
    private val jdbcTemplate: JdbcTemplate,
) : CustomOrderPointUsageRepository {

    companion object {
        private const val BATCH_SIZE = 100
    }

    /**
     * JDBC를 통한 Batch Insert 처리
     */
    override fun saveAllInBatch(usages: List<OrderPointUsage>) {
        val sql = """
           INSERT INTO order_point_usage (
                used_amount,
                order_id,
                point_id,
                created_at,
                modified_at
            ) VALUES (?, ?, ?, ?, ?)
        """.trimIndent()

        val now = LocalDateTime.now()

        jdbcTemplate.batchUpdate(
            sql,
            usages,
            BATCH_SIZE
        ) { ps: PreparedStatement, usage: OrderPointUsage ->

            ps.setLong(1, usage.usedAmount)
            ps.setLong(2, usage.order.id!!)
            ps.setLong(3, usage.pointRecord.id!!)
            ps.setTimestamp(4, Timestamp.valueOf(now))
            ps.setTimestamp(5, Timestamp.valueOf(now))
        }
    }

}
