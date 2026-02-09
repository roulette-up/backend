package kr.co.rouletteup.domain.roulette.repository

import java.time.LocalDate
import kr.co.rouletteup.domain.roulette.entity.DailyRoulette
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface DailyRouletteRepository : JpaRepository<DailyRoulette, Long> {
    fun findByRouletteDate(rouletteDate: LocalDate): DailyRoulette?
    fun existsByRouletteDate(rouletteDate: LocalDate): Boolean

    @Modifying
    @Query(
        """
        UPDATE daily_roulette
        SET used_budget = used_budget + :budget,
            participant_count = participant_count + 1
        WHERE roulette_date = :date
            AND used_budget + :budget <= total_budget
    """, nativeQuery = true
    )
    fun increaseUsedBudgetAndParticipant(
        @Param("date") date: LocalDate,
        @Param("budget") budget: Long,
    ): Int

}
