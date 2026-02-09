package kr.co.rouletteup.domain.order.repository

import kr.co.rouletteup.domain.order.dto.OrderWithNicknameDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomOrderRepository {
    fun findAllWithNicknameByUserId(userId: Long, pageable: Pageable): Page<OrderWithNicknameDto>
    fun findAllWithNicknameByProductId(productId: Long, pageable: Pageable): Page<OrderWithNicknameDto>
    fun findWithNicknameById(orderId: Long): OrderWithNicknameDto?
}