package reehi.board.articleread.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository


@Repository
class BoardArticleCountRepository (
    private val redisTemplate: StringRedisTemplate

){
    companion object {
        // article-read::board-article-count::board::{boardId}
        private const val KEY_FORMAT: String = "article-read::board-article-count::board::%s"
    }

    fun createOrUpdate(boardId: Long, articleCount: Long) {
        redisTemplate.opsForValue()[generateKey(boardId)] = articleCount.toString()
    }

    fun read(boardId: Long): Long {
        val result = redisTemplate.opsForValue()[generateKey(boardId)]
        return result?.toLong() ?: 0L
    }

    private fun generateKey(boardId: Long): String {
        return KEY_FORMAT.format(boardId)
    }
}