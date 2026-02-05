package kr.co.rouletteup.app.roulette.controller

import kr.co.rouletteup.app.roulette.usercase.GetRouletteUseCase
import kr.co.rouletteup.common.response.success.SuccessResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/roulettes")
class RouletteController(
    private val getRouletteUseCase: GetRouletteUseCase,
) {

    @GetMapping("/today")
    fun getTodayRoulette(): ResponseEntity<*> =
        ResponseEntity.ok(
            SuccessResponse.from(
                getRouletteUseCase.getTodayRemainingBudget()
            )
        )
}
