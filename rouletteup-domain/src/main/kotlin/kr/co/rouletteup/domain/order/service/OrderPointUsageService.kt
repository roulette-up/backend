package kr.co.rouletteup.domain.order.service

import kr.co.rouletteup.domain.order.entity.OrderPointUsage
import kr.co.rouletteup.domain.order.repository.OrderPointUsageRepository
import org.springframework.stereotype.Service

@Service
class OrderPointUsageService(
    private val orderPointUsageRepository: OrderPointUsageRepository,
) {

    fun saveAll(usages: List<OrderPointUsage>) {
        orderPointUsageRepository.saveAllInBatch(usages)
    }
}