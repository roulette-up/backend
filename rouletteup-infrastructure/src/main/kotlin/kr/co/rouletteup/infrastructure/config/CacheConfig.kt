package kr.co.rouletteup.infrastructure.config

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit
import kr.co.rouletteup.infrastructure.cache.constant.CacheNames
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableCaching
class CacheConfig(
    @Value("\${cache.caffeine.expire-minutes}")
    private val expireMinutes: Long,
) {

    @Bean
    fun cacheManager(): CacheManager {
        val manager = CaffeineCacheManager(
            CacheNames.REMAINING_BUDGET
        )

        manager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(expireMinutes, TimeUnit.MINUTES)
                .recordStats()
        )

        return manager
    }
}
