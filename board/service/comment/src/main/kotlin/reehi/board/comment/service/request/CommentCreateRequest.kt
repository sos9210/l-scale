package reehi.board.comment.service.request

class CommentCreateRequest (
    val articleId: Long,
    val content: String,
    val parentCommentId: Long,
    val writerId: Long,
) {
}