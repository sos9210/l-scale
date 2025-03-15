package reehi.board.articleread.repository

import reehi.board.articleread.client.ArticleClient
import reehi.board.common.event.payload.*
import java.time.LocalDateTime


class ArticleQueryModel(
    var articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime,
    var modifiedAt: LocalDateTime,
    var articleCommentCount: Long?,
    var articleLikeCount: Long?,
    
) {
    companion object {
        fun create(payload: ArticleCreatedEventPayload): ArticleQueryModel =
            ArticleQueryModel(
                articleId = payload.articleId,
                title = payload.title,
                content = payload.content,
                boardId = payload.boardId,
                writerId = payload.writerId,
                createdAt = payload.createdAt,
                modifiedAt = payload.modifiedAt,
                articleCommentCount = 0L,
                articleLikeCount = 0L,
            )

        fun create(article: ArticleClient.Companion.ArticleResponse, commentCount: Long?, likeCount: Long?): ArticleQueryModel =
            ArticleQueryModel(

                articleId = article.articleId,
                title = article.title,
                content = article.content,
                boardId = article.boardId,
                writerId = article.writerId,
                createdAt = article.createdAt,
                modifiedAt = article.modifiedAt,
                articleCommentCount = commentCount,
                articleLikeCount = likeCount,
            )

    }
    fun updateBy(payload: CommentCreatedEventPayload) {
        this.articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: CommentDeletedEventPayload) {
        this.articleCommentCount = payload.articleCommentCount
    }

    fun updateBy(payload: ArticleLikedEventPayload) {
        this.articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUnlikedEventPayload) {
        this.articleLikeCount = payload.articleLikeCount
    }

    fun updateBy(payload: ArticleUpdatedEventPayload) {
        title = payload.title
        content = payload.content
        boardId = payload.boardId
        writerId = payload.writerId
        createdAt = payload.createdAt
        modifiedAt = payload.modifiedAt
    }
}