package kr.co.rouletteup.admin.roulette.controller

import kr.co.rouletteup.admin.roulette.api.AdminRouletteApi
import kr.co.rouletteup.admin.roulette.usecase.GetRouletteForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/roulettes")
class AdminRouletteController(
    private val getRouletteForAdminUseCase: GetRouletteForAdminUseCase,
) : AdminRouletteApi {

    @GetMapping
    override fun getRoulettes(
        @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getRouletteForAdminUseCase.getRoulettes(pageable)
            )
        )

    @GetMapping("/today")
    override fun getTodayRoulette(): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getRouletteForAdminUseCase.getTodayRoulette()
            )
        )

}
