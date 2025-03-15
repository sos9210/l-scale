package reehi.board.articleread.client

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime

@Component
class ArticleClient(
    @Value("\${endpoints.reehi-board-article-service.url}")
    private val articleServiceUrl: String
) {
    private lateinit var restClient: RestClient


    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(articleServiceUrl!!)
    }

    fun read(articleId: Long): ArticleResponse? {
        try {
            val articleResponse = restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse::class.java)
            return articleResponse
        } catch (e: Exception) {
            println("[ArticleClient.read] articleId=$articleId")
            println(e)
            return null
        }
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse? {
        try {
            return restClient.get()
                .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".format(boardId, page, pageSize))
                .retrieve()
                .body(ArticlePageResponse::class.java)
        } catch (e: Exception) {
            println("[ArticleClient.readAll] boardId=$boardId, page=$page, pageSize=$pageSize")
            println( e)
            return ArticlePageResponse.EMPTY
        }
    }

    fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, pageSize: Long): List<ArticleResponse>? {
        try {
            return restClient.get()
                .uri(
                    if (lastArticleId != null) "/v1/articles/infinite-scroll?boardId=%s&lastArticleId=%s&pageSize=%s"
                        .format(
                            boardId,
                            lastArticleId,
                            pageSize
                        ) else "/v1/articles/infinite-scroll?boardId=%s&pageSize=%s"
                        .format(boardId, pageSize)
                )
                .retrieve()
                .body<List<ArticleResponse>>(object : ParameterizedTypeReference<List<ArticleResponse>?>() {})
        } catch (e: Exception) {
            println(
                "[ArticleClient.readAllInfiniteScroll] boardId=$boardId, lastArticleId=$lastArticleId, pageSize=$pageSize"
            )
            println(e)
            return listOf()
        }
    }

    fun count(boardId: Long): Long? {
        try {
            return restClient.get()
                .uri("/v1/articles/boards/{boardId}/count", boardId)
                .retrieve()
                .body(Long::class.java)
        } catch (e: Exception) {
            println("[ArticleClient.count] boardId=$boardId")
            println(e)
            return 0
        }
    }

    companion object {
        class ArticlePageResponse (
            val articles: List<ArticleResponse>,
            val articleCount: Long
        ){
            companion object {
                var EMPTY: ArticlePageResponse = ArticlePageResponse(listOf(), 0L)
            }
        }
        class ArticleResponse (
            val articleId: Long,
            val title: String,
            val content: String,
            val boardId: Long,
            val writerId: Long,
            val createdAt: LocalDateTime,
            val modifiedAt: LocalDateTime

        ){
        }
    }

}