package reehi.board.article.service.response

import reehi.board.article.entity.Article
import java.time.LocalDateTime

class ArticleResponse (
    val articleId: Long,
    var title: String,
    var content: String,
    var boardId: Long,
    var writerId: Long,
    var createdAt: LocalDateTime?,
    var modifiedAt: LocalDateTime?
) {
    companion object {
        fun from(article: Article): ArticleResponse =
            ArticleResponse(
                article.articleId,
                article.title,
                article.content,
                article.boardId,
                article.writerId,
                article.createdAt,
                article.modifiedAt,
            )
    }
}