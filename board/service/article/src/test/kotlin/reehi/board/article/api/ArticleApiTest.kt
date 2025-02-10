package reehi.board.article.api

import org.springframework.web.client.RestClient
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
