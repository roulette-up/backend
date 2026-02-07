package kr.co.rouletteup.domain.product.service

import kr.co.rouletteup.domain.product.entity.Product
import kr.co.rouletteup.domain.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {

    fun save(product: Product) {
        productRepository.save(product)
    }

    fun readById(id: Long): Product? =
        productRepository.findByIdOrNull(id)

    fun readAll(pageable: Pageable): Page<Product> =
        productRepository.findAll(pageable)

    fun decreaseStock(id: Long, amount: Int): Int =
        productRepository.decreaseStock(id, amount)
}
