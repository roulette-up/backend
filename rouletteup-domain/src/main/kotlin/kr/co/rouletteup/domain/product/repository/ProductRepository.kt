package kr.co.rouletteup.domain.product.repository

import kr.co.rouletteup.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ProductRepository : JpaRepository<Product, Long> {

    @Modifying
    @Query(
        value = """
        UPDATE product
        SET stock_quantity = stock_quantity - :amount
        WHERE id = :productId
            AND stock_quantity >= :amount
    """, nativeQuery = true
    )
    fun decreaseStock(
        @Param("productId") productId: Long,
        @Param("amount") amount: Int,
    ): Int

    @Modifying
    @Query(
        value = """
        UPDATE product
        SET stock_quantity = stock_quantity + :amount
        WHERE id = :productId
    """, nativeQuery = true
    )
    fun increaseStock(
        @Param("productId") productId: Long,
        @Param("amount") amount: Int,
    ): Int

}
