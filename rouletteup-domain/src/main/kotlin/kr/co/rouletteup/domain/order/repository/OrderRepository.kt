package kr.co.rouletteup.domain.order.repository

import kr.co.rouletteup.domain.order.entity.Order
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
    fun findAllByUserId(userId: Long, pageable: Pageable): Page<Order>
}