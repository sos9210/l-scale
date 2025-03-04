package reehi.board.hotarticle.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@Repository
class ArticleCreatedTimeRepository (
    val redisTemplate: StringRedisTemplate
){
    companion object {
        // hot-article::article::{articleId}::created-time

        private const val KEY_FORMAT: String = "hot-article::article::%s::created-time"
    }

    fun createOrUpdate(articleId: Long, createdAt: LocalDateTime, ttl: Duration) {
        redisTemplate.opsForValue().set(
            generateKey(articleId),
            createdAt.toInstant(ZoneOffset.UTC).toEpochMilli().toString(),
            ttl
        )
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): LocalDateTime? {
        val result = redisTemplate.opsForValue()[generateKey(articleId)] ?: return null
        return LocalDateTime.ofInstant(
            Instant.ofEpochMilli(result.toLong()), ZoneOffset.UTC
        )
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.formatted(articleId)
    }

}