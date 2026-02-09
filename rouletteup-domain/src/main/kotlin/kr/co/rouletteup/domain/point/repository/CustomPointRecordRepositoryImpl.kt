package kr.co.rouletteup.domain.point.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import java.time.LocalDate
import kr.co.rouletteup.domain.point.dto.PointRecordWithNicknameDto
import kr.co.rouletteup.domain.point.dto.UserPointDto
import kr.co.rouletteup.domain.point.entity.QPointRecord
import kr.co.rouletteup.domain.point.type.PointStatus
import kr.co.rouletteup.domain.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils

class CustomPointRecordRepositoryImpl(
    private val jpaQueryFactory: JPAQueryFactory,
) : CustomPointRecordRepository {

    val pointRecord = QPointRecord.pointRecord
    val user = QUser.user

    override fun findAllWithNicknameByUserId(
        userId: Long,
        pageable: Pageable,
    ): Page<PointRecordWithNicknameDto> {

        val content = jpaQueryFactory
            .select(
                Projections.constructor(
                    PointRecordWithNicknameDto::class.java,

                    pointRecord.id,
                    pointRecord.grantedPoint,
                    pointRecord.remainingPoint,
                    pointRecord.status,
                    pointRecord.expiresAt,
                    pointRecord.userId,
                    pointRecord.rouletteDate,
                    user.nickname
                )
            )
            .from(pointRecord)
            .join(user).on(user.id.eq(pointRecord.userId))
            .where(
                pointRecord.userId.eq(userId)
            )
            .orderBy(pointRecord.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = jpaQueryFactory
            .select(pointRecord.count())
            .from(pointRecord)
            .where(pointRecord.userId.eq(userId))

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun findAllWithNicknameByRouletteDate(
        date: LocalDate,
        pageable: Pageable,
    ): Page<PointRecordWithNicknameDto> {

        val content = jpaQueryFactory
            .select(
                Projections.constructor(
                    PointRecordWithNicknameDto::class.java,

                    pointRecord.id,
                    pointRecord.grantedPoint,
                    pointRecord.remainingPoint,
                    pointRecord.status,
                    pointRecord.expiresAt,
                    pointRecord.userId,
                    pointRecord.rouletteDate,
                    user.nickname
                )
            )
            .from(pointRecord)
            .join(user).on(user.id.eq(pointRecord.userId))
            .where(
                pointRecord.rouletteDate.eq(date)
            )
            .orderBy(pointRecord.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val countQuery = jpaQueryFactory
            .select(pointRecord.count())
            .from(pointRecord)
            .where(pointRecord.rouletteDate.eq(date))

        return PageableExecutionUtils.getPage(content, pageable) {
            countQuery.fetchOne() ?: 0L
        }
    }

    override fun findUserPointByUserId(userId: Long): UserPointDto? =
        jpaQueryFactory
            .select(
                Projections.constructor(
                    UserPointDto::class.java,
                    user.id,
                    pointRecord.remainingPoint.sum().coalesce(0L),
                    user.pointDebt
                )
            )
            .from(user)
            .leftJoin(pointRecord).on(
                pointRecord.userId.eq(user.id),
                pointRecord.status.eq(PointStatus.AVAILABLE)
            )
            .where(user.id.eq(userId))
            .groupBy(user.id, user.pointDebt)
            .fetchOne()

}
