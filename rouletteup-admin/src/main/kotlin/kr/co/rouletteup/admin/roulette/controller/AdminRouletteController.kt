package kr.co.rouletteup.admin.roulette.controller

import jakarta.validation.Valid
import kr.co.rouletteup.admin.roulette.api.AdminRouletteApi
import kr.co.rouletteup.admin.roulette.dto.AdminRouletteBudgetReq
import kr.co.rouletteup.admin.roulette.usecase.GetRouletteForAdminUseCase
import kr.co.rouletteup.admin.roulette.usecase.UpdateRouletteBudgetForAdminUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/admin/roulettes")
class AdminRouletteController(
    private val getRouletteForAdminUseCase: GetRouletteForAdminUseCase,
    private val updateRouletteBudgetForAdminUseCase: UpdateRouletteBudgetForAdminUseCase,
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

    @GetMapping("/future/budget")
    fun getFutureSettingsBudget(): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getRouletteForAdminUseCase.getFutureSettingsBudget()
            )
        )

    @PatchMapping("/today/budget")
    fun updateTodayBudget(
        @RequestBody @Valid request: AdminRouletteBudgetReq.UpdateToday,
    ): ResponseEntity<*> {
        updateRouletteBudgetForAdminUseCase.updateTodayBudget(request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

    @PatchMapping("/future/budget")
    fun upsertFutureBudget(
        @RequestBody @Valid request: AdminRouletteBudgetReq.UpdateFuture,
    ): ResponseEntity<*> {
        updateRouletteBudgetForAdminUseCase.upsertFutureBudget(request)
        return ResponseEntity.ok(SuccessResponse.ok())
    }

}
