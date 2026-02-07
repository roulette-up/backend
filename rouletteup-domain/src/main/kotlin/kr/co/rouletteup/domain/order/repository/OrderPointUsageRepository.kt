package kr.co.rouletteup.domain.order.repository

import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import org.springframework.data.jpa.repository.JpaRepository

interface OrderPointUsageRepository : JpaRepository<OrderPointUsage, Long>, CustomOrderPointUsageRepository {
}