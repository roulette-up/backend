package kr.co.rouletteup.domain.order.service

import kr.co.rouletteup.domain.order.entity.Order
import kr.co.rouletteup.domain.order.repository.OrderRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class OrderService(
    private val orderRepository: OrderRepository,
) {

    fun save(order: Order): Order =
        orderRepository.save(order)

    fun readById(id: Long): Order? =
        orderRepository.findByIdOrNull(id)

    fun readAllByUserId(userId: Long, pageable: Pageable): Page<Order> =
        orderRepository.findAllByUserId(userId, pageable)

}
