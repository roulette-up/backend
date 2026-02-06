package kr.co.rouletteup.domain.product.repository

import kr.co.rouletteup.domain.product.entity.Product
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
}