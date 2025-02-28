package reehi.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Table(name = "article_comment_count")
@Entity
class ArticleCommentCount (
    @Id
    val articleId: Long, // shard key
    val commentCount: Long

){

    companion object {
        fun init(articleId: Long, commentCount: Long): ArticleCommentCount =
            ArticleCommentCount(
                articleId = articleId,
                commentCount = commentCount

            )
    }
}