package reehi.board.like.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version

@Table(name = "article_like_count")
@Entity
class ArticleLikeCount (
    @Id
    var articleId: Long,
    var likeCount: Long,

    @Version
    var version: Long?

) {

    fun increase() {
        likeCount += 1
    }

    fun decrease() {
        likeCount -= 1
    }

    companion object {
        fun init(articleId: Long, likeCount: Long): ArticleLikeCount =
            ArticleLikeCount(
                articleId = articleId,
                likeCount = likeCount,
                version = null
            )

    }
}