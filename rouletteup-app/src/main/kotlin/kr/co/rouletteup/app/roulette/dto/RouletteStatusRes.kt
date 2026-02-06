package kr.co.rouletteup.app.roulette.dto

data class RouletteStatusRes(
    val participated: Boolean,
) {
    companion object {
        fun of(participated: Boolean): RouletteStatusRes =
            RouletteStatusRes(
                participated = participated
            )
    }
}