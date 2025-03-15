package reehi.board.articleread.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import reehi.board.common.dataserializer.DataSerializer
import java.util.*


@Component
class OptimizedCacheManager (
    val redisTemplate: StringRedisTemplate,
    val optimizedCacheLockProvider: OptimizedCacheLockProvider

){
    companion object {
        private const val DELIMITER = "::"
    }

    @Throws(Throwable::class)
    fun process(
        type: String,
        ttlSeconds: Long,
        args: Array<Any>,
        returnType: Class<*>,
        originDataSupplier: OptimizedCacheOriginDataSupplier<*>
    ): Any? {
        val key = generateKey(type, args)

        val cachedData = redisTemplate.opsForValue().get(key)
        if (cachedData.isNullOrEmpty()) {
            return refresh(originDataSupplier, key, ttlSeconds)
        }

        val optimizedCache = DataSerializer.deserialize(cachedData, OptimizedCache::class.java)
            ?: return refresh(originDataSupplier, key, ttlSeconds)

        if (!optimizedCache.isExpired()) {
            return optimizedCache.parseData(returnType)
        }

        if (!optimizedCacheLockProvider.lock(key)) {
            return optimizedCache.parseData(returnType)
        }

        return try {
            refresh(originDataSupplier, key, ttlSeconds)
        } finally {
            optimizedCacheLockProvider.unlock(key)
        }
    }

    @Throws(Throwable::class)
    private fun refresh(
        originDataSupplier: OptimizedCacheOriginDataSupplier<*>,
        key: String,
        ttlSeconds: Long
    ): Any {
        val result = originDataSupplier.get()

        val optimizedCacheTTL = OptimizedCacheTTL.of(ttlSeconds)
        val optimizedCache = OptimizedCache.of(result!!, optimizedCacheTTL.logicalTTL)

        redisTemplate.opsForValue().set(
            key,
            DataSerializer.serialize(optimizedCache)!!,
            optimizedCacheTTL.physicalTTL
        )

        return result
    }

    private fun generateKey(prefix: String, args: Array<Any>): String {
        return prefix + DELIMITER + args.joinToString(DELIMITER) { it.toString() }
    }

}