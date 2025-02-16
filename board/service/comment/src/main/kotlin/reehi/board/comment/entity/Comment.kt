package reehi.board.comment.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "comment")
@Entity
class Comment(
    @Id
    val commentId: Long,
    var content: String,
    var parentCommentId: Long?,
    val articleId: Long, //shard key
    val writerId: Long,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
    ){
    fun isRoot(): Boolean = parentCommentId == commentId

    fun delete() {
        deleted = true
    }
    companion object {
        fun create(
            commentId: Long,
            content: String,
            parentCommentId: Long?,
            articleId: Long,
            writerId: Long
            ): Comment =
            Comment(
                commentId = commentId,
                content = content,
                parentCommentId = parentCommentId,
                articleId = articleId,
                writerId = writerId,
                deleted = false,
                createdAt = LocalDateTime.now(),
            )

    }
}