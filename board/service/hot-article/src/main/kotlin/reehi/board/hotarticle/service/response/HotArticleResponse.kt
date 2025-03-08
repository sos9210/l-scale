package reehi.board.hotarticle.service.response

import reehi.board.hotarticle.client.ArticleClient
import java.time.LocalDateTime


class HotArticleResponse (
    var articleId: Long,
    var title: String,
    var createdAt: LocalDateTime

){

    companion object {
        fun from(articleResponse: ArticleClient.Companion.ArticleResponse): HotArticleResponse =
            HotArticleResponse(
                articleId = articleResponse.articleId,
                title = articleResponse.title,
                createdAt = articleResponse.createdAt
            )


    }
}