package reehi.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration


@Repository
class ArticleViewDistributedLockRepository (
    val redisTemplate: StringRedisTemplate

){
    companion object{
        // view::article::{article_id}::user::{user_id}::lock
        val KEY_FORMAT: String = "view::article::%s::user::%s::lock"
    }

    fun lock(articleId: Long, userId: Long, ttl: Duration): Boolean {
        val key = generateKey(articleId, userId)
        return redisTemplate.opsForValue().setIfAbsent(key, "", ttl) ?: false
    }

    private fun generateKey(articleId: Long, userId: Long): String {
        return KEY_FORMAT.formatted(articleId, userId)
    }
}