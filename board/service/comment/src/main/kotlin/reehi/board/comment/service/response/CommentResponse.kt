package reehi.board.comment.service.response

import reehi.board.comment.entity.Comment
import java.time.LocalDateTime

class CommentResponse(
    val commentId: Long,
    var content: String,
    var parentCommentId: Long?,
    val articleId: Long,
    val writerId: Long,
    var deleted: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun from(comment: Comment): CommentResponse =
            CommentResponse(
                comment.commentId,
                comment.content,
                comment.parentCommentId,
                comment.articleId,
                comment.writerId,
                comment.deleted,
                comment.createdAt
            )

    }
}