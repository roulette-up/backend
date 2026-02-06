package kr.co.rouletteup.domain.user.service

import kr.co.rouletteup.domain.user.entity.User
import kr.co.rouletteup.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun save(user: User): User =
        userRepository.save(user)

    fun readByNickname(nickname: String): User? =
        userRepository.findByNickname(nickname)

    fun updatePointWithDebt(id: Long, point: Long) {
        userRepository.updatePointWithDebt(id, point)
    }

}
