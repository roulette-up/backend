package kr.co.rouletteup.app.roulette.dto

data class RouletteParticipateRes(
    val reward: Long,
    val credit: Long,
) {
    companion object {
        fun of(reward: Long, credit: Long): RouletteParticipateRes =
            RouletteParticipateRes(
                reward = reward,
                credit = credit
            )
    }
}
