package kr.co.rouletteup.infrastructure.cache.repository

import kotlin.reflect.KClass
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Repository

@Repository
class CaffeineCacheRepository(
    private val cacheManager: CacheManager
) : CacheRepository {

    override fun <T : Any> get(cacheName: String, key: Any, type: KClass<T>): T? {
        val cache = requireCache(cacheName)
        return cache.get(key, type.javaObjectType)
    }

    override fun put(cacheName: String, key: Any, value: Any) {
        val cache = requireCache(cacheName)
        cache.put(key, value)
    }

    override fun evict(cacheName: String, key: Any) {
        val cache = requireCache(cacheName)
        cache.evict(key)
    }

    override fun clear(cacheName: String) {
        val cache = requireCache(cacheName)
        cache.clear()
    }

    /**
     * 캐시 인스턴스 조회 메서드
     */
    private fun requireCache(cacheName: String): Cache =
        requireNotNull(cacheManager.getCache(cacheName)) {
            "Cache not found: ${cacheName}"
        }
}
