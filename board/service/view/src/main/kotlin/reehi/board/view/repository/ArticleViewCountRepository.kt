package reehi.board.view.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository


@Repository
class ArticleViewCountRepository (
    val redisTemplate: StringRedisTemplate
){


    fun read(articleId: Long): Long {
        return redisTemplate.opsForValue()[generateKey(articleId)]?.toLong() ?: 0L
    }

    fun increase(articleId: Long): Long {
        return redisTemplate.opsForValue().increment(generateKey(articleId)) ?: 0L
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.formatted(articleId)
    }

    companion object {
        // view::article::{article_id}::view_count
        const val KEY_FORMAT: String = "view::article::%s::view_count"
    }
}