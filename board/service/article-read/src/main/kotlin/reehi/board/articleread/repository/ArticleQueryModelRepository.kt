package reehi.board.articleread.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import reehi.board.common.dataserializer.DataSerializer
import java.time.Duration

@Repository
class ArticleQueryModelRepository (
    val redisTemplate: StringRedisTemplate
){

    companion object {
        // article-read::article::{articleId}
        private const val KEY_FORMAT = "article-read::article::%s"
    }

    fun create(articleQueryModel: ArticleQueryModel, ttl: Duration) {
        DataSerializer.serialize(articleQueryModel)?.let {
            redisTemplate.opsForValue().set(
                generateKey(articleQueryModel),
                it,
                ttl
            )
        }
    }

    fun update(articleQueryModel: ArticleQueryModel) {
        DataSerializer.serialize(articleQueryModel)?.let {
            redisTemplate.opsForValue().setIfPresent(
                generateKey(articleQueryModel),
                it
            )
        }
    }

    fun delete(articleId: Long) {
        redisTemplate.delete(generateKey(articleId))
    }

    fun read(articleId: Long): ArticleQueryModel? {
        val json = redisTemplate.opsForValue().get(generateKey(articleId))
        return json?.let { DataSerializer.deserialize(it, ArticleQueryModel::class.java) }
    }

    private fun generateKey(articleQueryModel: ArticleQueryModel): String {
        return generateKey(articleQueryModel.articleId)
    }

    private fun generateKey(articleId: Long): String {
        return KEY_FORMAT.format(articleId)
    }

    fun readAll(articleIds: List<Long>?): Map<Long, ArticleQueryModel> {
        val keyList = articleIds?.map { generateKey(it) }
        val values = keyList?.let { redisTemplate.opsForValue().multiGet(it) }

        return values.orEmpty()
            .filterNotNull()
            .mapNotNull { DataSerializer.deserialize(it, ArticleQueryModel::class.java) }
            .associateBy { it.articleId }
    }
}