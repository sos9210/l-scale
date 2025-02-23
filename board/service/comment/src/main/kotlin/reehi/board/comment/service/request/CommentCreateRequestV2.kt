package reehi.board.comment.service.request

class CommentCreateRequestV2 (
    val articleId: Long,
    val content: String,
    val parentPath: String?,
    val writerId: Long,
) {
}