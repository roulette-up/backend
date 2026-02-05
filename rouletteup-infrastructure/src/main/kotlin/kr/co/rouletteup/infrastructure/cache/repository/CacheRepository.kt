package kr.co.rouletteup.infrastructure.cache.repository

import kotlin.reflect.KClass

interface CacheRepository {
    /**
     * 캐시 값 조회
     */
    fun <T : Any> get(cacheName: String, key: Any, type: KClass<T>): T?

    /**
     * 캐시 값 삽입
     */
    fun put(cacheName: String, key: Any, value: Any)

    /**
     * 특정 키 캐시 삭제
     */
    fun evict(cacheName: String, key: Any)

    /**
     * 해당 캐시 공간 전체 삭제
     */
    fun clear(cacheName: String)
}
