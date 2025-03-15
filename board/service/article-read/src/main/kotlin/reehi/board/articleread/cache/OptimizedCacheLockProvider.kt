package reehi.board.articleread.cache

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class OptimizedCacheLockProvider(
    val redisTemplate: StringRedisTemplate
) {
    companion object {
        val KEY_PREFIX: String = "optimized-cache-lock::"
        val LOCK_TTL: Duration = Duration.ofSeconds(3)

    }

    fun lock(key: String): Boolean {
        return redisTemplate.opsForValue().setIfAbsent(
            generateLockKey(key),
            "",
            LOCK_TTL
        ) ?: false
    }

    fun unlock(key: String) {
        redisTemplate.delete(generateLockKey(key))
    }

    private fun generateLockKey(key: String): String {
        return KEY_PREFIX + key
    }
}