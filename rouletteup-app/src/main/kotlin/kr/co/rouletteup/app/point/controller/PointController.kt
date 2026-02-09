package kr.co.rouletteup.app.point.controller

import kr.co.rouletteup.app.point.api.PointApi
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
@RequestMapping("/api/v1")
class PointController(
    private val getPointRecordUseCase: GetPointRecordUseCase,
) : PointApi {

    @GetMapping("/points/records")
    override fun getMyRecords(
        @RequestHeader(value = "X-User-Id") userId: Long,
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getPointRecordUseCase.getMyRecords(userId, pageable)
            )
        )

    @GetMapping("/users/points")
    override fun getUserPointByUserId(
        @RequestHeader(value = "X-User-Id") userId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getPointRecordUseCase.getUserPointByUserId(userId)
            )
        )
}
