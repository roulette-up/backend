package kr.co.rouletteup.domain.user.repository

import kr.co.rouletteup.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?

    @Modifying
    @Query("""
        UPDATE users
        SET available_point = available_point + GREATEST(0, :point - point_debt),
            point_debt = GREATEST(0, point_debt - :point)
        WHERE id = :userId
            AND :point > 0
    """, nativeQuery = true)
    fun updatePointWithDebt(
        @Param("userId") userId: Long,
        @Param("point") point: Long,
    )

}
