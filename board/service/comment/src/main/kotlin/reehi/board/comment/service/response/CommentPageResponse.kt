package reehi.board.comment.service.response

class CommentPageResponse (
    val comments: List<CommentResponse>,
    val commentCount: Long
){
    companion object{
        fun of(comments: List<CommentResponse>, commentCount: Long) : CommentPageResponse =
            CommentPageResponse(comments, commentCount)
    }
}