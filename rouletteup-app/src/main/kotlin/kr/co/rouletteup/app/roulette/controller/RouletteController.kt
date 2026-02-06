package kr.co.rouletteup.app.roulette.controller

import kr.co.rouletteup.app.roulette.api.RouletteApi
import kr.co.rouletteup.app.roulette.usecase.CheckRouletteParticipationUseCase
import kr.co.rouletteup.app.roulette.usecase.GetRouletteUseCase
import kr.co.rouletteup.app.roulette.usecase.ParticipateRouletteUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/roulettes")
class RouletteController(
    private val getRouletteUseCase: GetRouletteUseCase,
    private val participateRouletteUseCase: ParticipateRouletteUseCase,
    private val checkRouletteParticipationUseCase: CheckRouletteParticipationUseCase,
) : RouletteApi {

    @PostMapping("/today/participation")
    override fun participate(
        @RequestHeader(value = "X-User-Id") userId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                participateRouletteUseCase.participate(userId)
            )
        )

    @GetMapping("/today")
    override fun getTodayBudget(): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getRouletteUseCase.getTodayBudget()
            )
        )

    @GetMapping("/today/participation")
    override fun checkTodayParticipation(
        @RequestHeader(value = "X-User-Id") userId: Long,
    ): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                checkRouletteParticipationUseCase.checkTodayParticipation(userId)
            )
        )
}
