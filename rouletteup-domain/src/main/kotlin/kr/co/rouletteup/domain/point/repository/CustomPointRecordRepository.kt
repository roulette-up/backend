package kr.co.rouletteup.domain.point.repository

import java.time.LocalDate
import kr.co.rouletteup.domain.point.dto.PointRecordWithNicknameDto
import kr.co.rouletteup.domain.point.dto.UserPointDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomPointRecordRepository {
    fun findAllWithNicknameByUserId(userId: Long, pageable: Pageable): Page<PointRecordWithNicknameDto>
    fun findAllWithNicknameByRouletteDate(date: LocalDate, pageable: Pageable): Page<PointRecordWithNicknameDto>
    fun findUserPointByUserId(userId: Long): UserPointDto?
}
