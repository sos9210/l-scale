package reehi.board.like.service.response

import reehi.board.like.entity.ArticleLike
import java.time.LocalDateTime

class ArticleLikeResponse (
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
){
    companion object {
        fun from(articleLike: ArticleLike): ArticleLikeResponse =
            ArticleLikeResponse(
                articleLike.articleLikeId,
                articleLike.articleId,
                articleLike.userId,
                articleLike.createdAt
            )
    }
}