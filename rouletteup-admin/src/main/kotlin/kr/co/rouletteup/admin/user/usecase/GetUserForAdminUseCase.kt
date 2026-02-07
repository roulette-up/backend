package kr.co.rouletteup.admin.user.usecase

import kr.co.rouletteup.admin.user.dto.AdminUserDetail
import kr.co.rouletteup.admin.user.dto.AdminUserSummary
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import kr.co.rouletteup.domain.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetUserForAdminUseCase(
    private val userService: UserService,
) {

    @Transactional(readOnly = true)
    fun getUsers(pageable: Pageable): Page<AdminUserSummary> =
        userService.readAllIncludingDeleted(pageable)
            .map { user -> AdminUserSummary.from(user) }

    @Transactional(readOnly = true)
    fun getUserById(userId: Long): AdminUserDetail {
        val user = userService.readByIdIncludeDeleted(userId)
            ?: throw UserException(UserErrorType.NOT_FOUND)

        return AdminUserDetail.from(user)
    }

}
