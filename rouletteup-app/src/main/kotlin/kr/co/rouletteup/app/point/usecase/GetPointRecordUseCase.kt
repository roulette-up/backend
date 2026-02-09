package kr.co.rouletteup.app.point.usecase

import kr.co.rouletteup.app.point.dto.PointRecordRes
import kr.co.rouletteup.app.point.dto.UserPointRes
import kr.co.rouletteup.domain.point.service.PointRecordService
import kr.co.rouletteup.domain.user.exception.UserErrorType
import kr.co.rouletteup.domain.user.exception.UserException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GetPointRecordUseCase(
    private val pointRecordService: PointRecordService,
) {

    /**
     * 사용자 포인트 내역 조회 메서드
     *
     * @param userId 조회할 사용자 ID(PK)
     * @return 포인트 내역 Page
     */
    @Transactional(readOnly = true)
    fun getMyRecords(
        userId: Long, pageable: Pageable,
    ): Page<PointRecordRes> =
        pointRecordService.readAllByUserId(userId, pageable)
            .map { record -> PointRecordRes.from(record) }

    @Transactional(readOnly = true)
    fun getUserPointByUserId(userId: Long): UserPointRes {
        val userPoint = pointRecordService.readUserPointByUserId(userId)
            ?: throw UserException(UserErrorType.NOT_FOUND)

        return UserPointRes.from(userPoint)
    }

}
