package reehi.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class ArticleLike(
    @Id
    val articleLikeId: Long,
    val articleId: Long,
    val userId: Long,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun create(articleLikeId: Long, articleId: Long, userId: Long): ArticleLike =
            ArticleLike(articleLikeId, articleId, userId, LocalDateTime.now())
    }
}