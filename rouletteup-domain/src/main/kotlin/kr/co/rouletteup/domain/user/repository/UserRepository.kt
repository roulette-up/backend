package kr.co.rouletteup.domain.user.repository

import kr.co.rouletteup.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByNickname(nickname: String): User?
}
