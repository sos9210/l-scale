package reehi.board.article.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import reehi.board.article.service.response.ArticlePageResponse
import reehi.board.article.service.response.ArticleResponse
import kotlin.test.Test

class ArticleApiTest (
){
    val client : RestClient =
        RestClient.create("http://localhost:9000")

    @Test
    fun createTest() {
        val response = create(
            ArticleCreateRequest(
                "hi", "mycontent", 1L, 1L
            )
        )
        println("response = ${response!!.articleId} ," +
                "${response!!.title}," +
                "${response!!.content}," +
                "${response!!.writerId}," +
                "${response!!.boardId}," +
                "${response.createdAt}")
    }
    @Test
    fun readTest() {
        val response = read(147350121195876352);
        println("response = ${response!!.title}")
    }
    @Test
    fun updateTest() {
        update(147350121195876352);
        val response = read(147350121195876352)
        println("response = ${response!!.title}")
    }
    @Test
    fun deleteTest() {
        client.delete()
            .uri("/v1/articles/147350121195876352 ")
            .retrieve()
            .toEntity(ArticleResponse::class.java)
    }

    @Test
    fun readAllTest() {
        val response = client.get()
            .uri("/v1/articles?boardId=1&pageSize=30&page=50000")
            .retrieve()
            .body(ArticlePageResponse::class.java)

        println("response = ${response!!.articleCount}")
        for (article in response.articles) {
            println("article = ${article.title}")
        }
    }

    fun update(articleId: Long) {
        client.put()
            .uri("/v1/articles/$articleId")
            .body(ArticleUpdateRequest("hi222","my content 222"))
            .retrieve()
            .toEntity(ArticleResponse::class.java)
    }
    fun read(articleId: Long) : ArticleResponse? {
        return client.get().uri("/v1/articles/$articleId")
            .retrieve()
            .body(ArticleResponse::class.java)
    }

    fun create(request: ArticleCreateRequest): ArticleResponse? {
        return client.post().uri("/v1/articles")
            .body(request)
            .retrieve()
            .body(ArticleResponse::class.java)

    }

    @Test
    fun readAllInfiniteScrollTest() {
        val articles1 = client.get().uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})

        for (article in articles1!!) {
            println("article1.articleId = ${article.articleId}")
        }
        println("")
        val lastArticleId = articles1.last().articleId

        val articles2 = client.get().uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId=$lastArticleId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<ArticleResponse>>() {})

        for (article in articles2!!) {
            println("article2.articleId = ${article.articleId}")
        }

    }
    companion object {
        class ArticleCreateRequest(
            val title: String,
            val content: String,
            val writerId: Long,
            val boardId: Long,
        )
        class ArticleUpdateRequest(
            val title: String,
            val content: String,
        )

    }
}
