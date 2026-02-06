package kr.co.rouletteup.app.roulette.dto

data class RouletteParticipateRes(
    val reward: Long,
) {
    companion object {
        fun of(reward: Long): RouletteParticipateRes =
            RouletteParticipateRes(
                reward = reward
            )
    }
}
