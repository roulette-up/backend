package kr.co.rouletteup.admin.point.controller

import java.time.LocalDate
import kr.co.rouletteup.admin.point.usecase.GetPointForAdminUseCase
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
@RequestMapping("/api/v1/admin/points")
class AdminPointController(
    private val getPointForAdminUseCase: GetPointForAdminUseCase,
) {

    @GetMapping("/users/{userId}")
    fun getPointRecordByUserId(
        @PathVariable userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getPointForAdminUseCase.getPointRecordByUserId(userId, pageable)
            )
        )

    @GetMapping("/roulettes/{rouletteDate}")
    fun getPointRecordByRouletteDate(
        @PathVariable rouletteDate: LocalDate,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getPointForAdminUseCase.getPointRecordByRouletteDate(rouletteDate, pageable)
            )
        )
}
