package kr.co.rouletteup.app.point.controller

import kr.co.rouletteup.app.point.usecase.GetPointRecordUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/points")
class PointController(
    private val getPointRecordUseCase: GetPointRecordUseCase,
) {

    @GetMapping("/records")
    fun getMyRecords(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getPointRecordUseCase.getMyRecords(userId, pageable)
            )
        )
}
