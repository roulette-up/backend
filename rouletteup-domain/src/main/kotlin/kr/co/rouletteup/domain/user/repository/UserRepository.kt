package kr.co.rouletteup.domain.user.repository

import kr.co.rouletteup.domain.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?

    @Query(
        value = """
            SELECT *
            FROM users
            ORDER BY id DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM users
        """, nativeQuery = true
    )
    fun findAllIncludeDeleted(pageable: Pageable): Page<User>

    @Query(
        value = """
        SELECT *
        FROM users
        WHERE id = :userId
    """, nativeQuery = true
    )
    fun findByIdIncludeDeleted(@Param("userId") userId: Long): User?
}
