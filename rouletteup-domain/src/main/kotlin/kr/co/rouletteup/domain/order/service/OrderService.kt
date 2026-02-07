package kr.co.rouletteup.domain.order.service

import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.repository.OrderRepository
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {

    fun save(order: Order): Order =
        orderRepository.save(order)
}
