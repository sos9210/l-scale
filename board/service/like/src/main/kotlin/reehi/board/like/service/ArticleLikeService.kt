package reehi.board.like.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.like.entity.ArticleLike
import reehi.board.like.repository.ArticleLikeRepository
import reehi.board.like.service.response.ArticleLikeResponse

@Service
class ArticleLikeService (
    val articleLikeRepository: ArticleLikeRepository
){
    val snowflake: Snowflake = Snowflake()

    fun read(articleId: Long, userId: Long): ArticleLikeResponse {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let { ArticleLikeResponse.from(it) }
            ?: throw NoSuchElementException("ArticleLike not found")
    }


    @Transactional
    fun like(articleId: Long, userId: Long) {
        articleLikeRepository.save(
            ArticleLike.create(
                snowflake.nextId(),
                articleId,
                userId
            )
        )
    }

    @Transactional
    fun unlike(articleId: Long, userId: Long) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            ?.let { articleLikeRepository.delete(it) }
    }
}