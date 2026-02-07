package kr.co.rouletteup.domain.order.repository

import kr.co.rouletteup.domain.order.entity.OrderPointUsage

interface CustomOrderPointUsageRepository {
    fun saveAllInBatch(usages: List<OrderPointUsage>)
}