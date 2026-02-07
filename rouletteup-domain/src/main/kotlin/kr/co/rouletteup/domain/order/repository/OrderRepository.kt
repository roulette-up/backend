package kr.co.rouletteup.domain.order.repository

import kr.co.rouletteup.domain.order.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {
}