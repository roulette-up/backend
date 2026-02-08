package kr.co.rouletteup.internal.controller

import kr.co.rouletteup.common.response.success.SuccessResponse
import kr.co.rouletteup.infrastructure.cache.constant.CacheNames
import kr.co.rouletteup.infrastructure.cache.repository.CacheRepository
import kr.co.rouletteup.internal.api.AdminCacheEvictApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 서버 내부 통신 API
 */
@RestController
@RequestMapping("/internal/v1/admin/cache")
class AdminCacheEvictController(
    private val cacheRepository: CacheRepository,
) : AdminCacheEvictApi {

    @DeleteMapping("/total-budget/{date}")
    override fun evictTotalBudget(
        @PathVariable date: String,
    ): ResponseEntity<*> {
        cacheRepository.evict(CacheNames.TOTAL_BUDGET, date)
        return ResponseEntity.ok(SuccessResponse.ok())
    }
}