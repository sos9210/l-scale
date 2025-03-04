package reehi.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleLikeCountRepository(
    val redisTemplate: StringRedisTemplate
) {
    companion object {
        // hot-article::article::{articleId}::like-count
        private const val KEY_FORMAT: String = "hot-article::article::%s::like-count"

    }
    fun createOrUpdate(articleId: Long, likeCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), likeCount.toString(), ttl)
    }

    fun read(articleId: Long): Long {
        val result = redisTemplate.opsForValue()[generateKey(articleId)]
        return result?.toLong() ?: 0L
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.formatted(articleId)
    }
}