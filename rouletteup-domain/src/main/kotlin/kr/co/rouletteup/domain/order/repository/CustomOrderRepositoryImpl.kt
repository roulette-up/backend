package kr.co.rouletteup.domain.order.repository

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.rouletteup.domain.order.dto.OrderWithNicknameDto
import kr.co.rouletteup.domain.order.entity.QOrder
import kr.co.rouletteup.domain.user.entity.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class CustomOrderRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CustomOrderRepository {

    val order = QOrder.order
    val user = QUser.user

    override fun findAllWithNicknameByUserId(
        userId: Long,
        pageable: Pageable,
    ): Page<OrderWithNicknameDto> {

        val content = queryFactory
            .select(
                Projections.constructor(
                    OrderWithNicknameDto::class.java,
                    order.id,
                    order.quantity,
                    order.productPrice,
                    order.productName,
                    order.status,
                    user.id,
                    order.product.id,
                    order.createdAt,
                    user.nickname,
                )
            )
            .from(order)
            .join(order.user, user)
            .where(user.id.eq(userId))
            .orderBy(order.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(order.id.count())
            .from(order)
            .where(order.user.id.eq(userId))
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findAllWithNicknameByProductId(
        productId: Long,
        pageable: Pageable,
    ): Page<OrderWithNicknameDto> {
        val content = queryFactory
            .select(
                Projections.constructor(
                    OrderWithNicknameDto::class.java,
                    order.id,
                    order.quantity,
                    order.productPrice,
                    order.productName,
                    order.status,
                    user.id,
                    order.product.id,
                    order.createdAt,
                    user.nickname,
                )
            )
            .from(order)
            .join(order.user, user)
            .where(order.product.id.eq(productId))
            .orderBy(order.id.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val total = queryFactory
            .select(order.id.count())
            .from(order)
            .where(order.product.id.eq(productId))
            .fetchOne() ?: 0L

        return PageImpl(content, pageable, total)
    }

    override fun findWithNicknameById(orderId: Long): OrderWithNicknameDto? {
        return queryFactory
            .select(
                Projections.constructor(
                    OrderWithNicknameDto::class.java,
                    order.id,
                    order.quantity,
                    order.productPrice,
                    order.productName,
                    order.status,
                    user.id,
                    order.product.id,
                    order.createdAt,
                    user.nickname,
                )
            )
            .from(order)
            .join(order.user, user)
            .where(order.id.eq(orderId))
            .fetchOne()
    }

}
