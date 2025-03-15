package reehi.board.common.outboxmessagerelay

import jakarta.annotation.PreDestroy
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.connection.RedisConnection
import org.springframework.data.redis.connection.StringRedisConnection
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


@Component
class MessageRelayCoordinator (
    val redisTemplate: StringRedisTemplate,

    @Value("\${spring.application.name}")
    private val applicationName: String,

    val APP_ID: String = UUID.randomUUID().toString()

) {


    fun assignShards(): AssignedShard {
        return AssignedShard.of(APP_ID, findAppIds(), MessageRelayConstants.SHARD_COUNT.toLong())
    }

    private fun findAppIds(): List<String> {
        return redisTemplate.opsForZSet().reverseRange(generateKey(), 0, -1)!!.stream()
            .sorted()
            .toList()
    }

    @Scheduled(fixedDelay = PING_INTERVAL_SECONDS.toLong(), timeUnit = TimeUnit.SECONDS)
    fun ping() {
        redisTemplate.executePipelined(RedisCallback<Any> { action: RedisConnection ->
            val conn = action as StringRedisConnection
            val key = generateKey()
            conn.zAdd(key, Instant.now().toEpochMilli().toDouble(), APP_ID)
            conn.zRemRangeByScore(
                key,
                Double.NEGATIVE_INFINITY,
                Instant.now().minusSeconds((PING_INTERVAL_SECONDS * PING_FAILURE_THRESHOLD).toLong()).toEpochMilli()
                    .toDouble()
            )
            null
        } as RedisCallback<*>)
    }

    @PreDestroy
    fun leave() {
        redisTemplate.opsForZSet().remove(generateKey(), APP_ID)
    }

    private fun generateKey(): String {
        return "message-relay-coordinator::app-list::%s".formatted(applicationName)
    }

    companion object {
        private const val PING_INTERVAL_SECONDS = 3
        private const val PING_FAILURE_THRESHOLD = 3
    }
}