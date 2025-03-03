package reehi.board.hotarticle.repository

import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Repository
class HotArticleListRepository (
    val redisTemplate: StringRedisTemplate
){
    companion object {
        // hot-article::list::{yyyyMMdd}
        private val KEY_FORMAT: String = "hot-article::list::%s"

        private val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

    }

    fun add(articleId: Long, time: LocalDateTime, score: Long, limit: Long, ttl: Duration) {
        redisTemplate.executePipelined(RedisCallback<Any> { action: RedisConnection ->
            val conn = action as StringRedisConnection
            val key = generateKey(time)
            conn.zAdd(key, score.toDouble(), articleId.toString())
            conn.zRemRange(key, 0, - limit - 1)
            conn.expire(key, ttl.toSeconds())
        } as RedisCallback<*>)
    }

    fun remove(articleId: Long, time: LocalDateTime) {
        redisTemplate.opsForZSet().remove(generateKey(time), articleId.toString())
    }

    private fun generateKey(time: LocalDateTime): String {
        return generateKey(TIME_FORMATTER.format(time))
    }

    private fun generateKey(dateStr: String): String {
        return KEY_FORMAT.formatted(dateStr)
    }

    fun readAll(dateStr: String): List<Long> {
        return redisTemplate.opsForZSet()
            .reverseRangeWithScores(generateKey(dateStr), 0, -1)!!
            .stream()
            .peek { tuple ->
                println("[HotArticleListRepository.readAll] articleId=${tuple.value}, score=${tuple.score}")
            }
            .map { tuple -> tuple.value!!.toLong() }
            .toList()
    }
}