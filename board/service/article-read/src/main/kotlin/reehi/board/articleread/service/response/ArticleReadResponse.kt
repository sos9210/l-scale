package reehi.board.articleread.service.response

import reehi.board.articleread.repository.ArticleQueryModel
import java.time.LocalDateTime



class ArticleReadResponse (
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val articleCommentCount: Long,
    val articleLikeCount: Long,
    val articleViewCount: Long,
    
){
    companion object {
        fun from(articleQueryModel: ArticleQueryModel, viewCount: Long): ArticleReadResponse =
            ArticleReadResponse(
            articleId = articleQueryModel.articleId,
            title = articleQueryModel.title,
            content = articleQueryModel.content,
            boardId = articleQueryModel.boardId,
            writerId = articleQueryModel.writerId,
            createdAt = articleQueryModel.createdAt,
            modifiedAt = articleQueryModel.modifiedAt,
            articleCommentCount = articleQueryModel.articleCommentCount?:0,
            articleLikeCount = articleQueryModel.articleLikeCount?:0,
            articleViewCount = viewCount
            )
    }

}