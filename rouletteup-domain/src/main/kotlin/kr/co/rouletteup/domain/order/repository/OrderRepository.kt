package kr.co.rouletteup.domain.order.repository

import kr.co.rouletteup.domain.order.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Order>

    @Query(
        value = """
            SELECT *
            FROM orders
            WHERE user_id = :userId
            ORDER BY id DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM orders
            WHERE user_id = :userId
        """, nativeQuery = true
    )
    fun findAllByUserIdIdIncludeDeleted(
        @Param("userId") userId: Long,
        pageable: Pageable
    ): Page<Order>

    @Query(
        value = """
            SELECT *
            FROM orders
            WHERE product_id = :productId
            ORDER BY id DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM orders
            WHERE product_id = :productId
        """, nativeQuery = true
    )
    fun findAllByProductIdIdIncludeDeleted(
        @Param("productId") productId: Long,
        pageable: Pageable
    ): Page<Order>

    @Query(
        value = """
        SELECT *
        FROM orders
        WHERE id = :orderId
    """, nativeQuery = true
    )
    fun findByIdIncludeDeleted(@Param("orderId") orderId: Long): Order?
}
