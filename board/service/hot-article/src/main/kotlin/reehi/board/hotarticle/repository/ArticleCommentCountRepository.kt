package reehi.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration

@Repository
class ArticleCommentCountRepository(
    val redisTemplate: StringRedisTemplate,
) {
    companion object {
        // hot-article::article::{articleId}::comment-count
        private const val KEY_FORMAT: String = "hot-article::article::%s::comment-count"
    }

    fun createOrUpdate(articleId: Long, commentCount: Long, ttl: Duration) {
        redisTemplate.opsForValue().set(generateKey(articleId), commentCount.toString(), ttl)
    }

    fun read(articleId: Long): Long {
        val result = redisTemplate.opsForValue()[generateKey(articleId)]
        return result?.toLong() ?: 0L
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.formatted(articleId)
    }
}