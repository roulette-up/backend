package kr.co.rouletteup.admin.user.controller

import kr.co.rouletteup.admin.user.api.AdminUserApi
import kr.co.rouletteup.admin.user.usecase.GetUserForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/users")
class AdminUserController(
    private val getUserForAdminUseCase: GetUserForAdminUseCase,
) : AdminUserApi {

    @GetMapping
    override fun getUsers(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getUserForAdminUseCase.getUsers(pageable)
            )
        )

    @GetMapping("/{userId}")
    override fun getUserById(
        @PathVariable userId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getUserForAdminUseCase.getUserById(userId)
            )
        )
}