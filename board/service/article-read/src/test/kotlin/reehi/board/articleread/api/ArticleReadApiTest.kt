package reehi.board.articleread.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import reehi.board.articleread.service.response.ArticleReadPageResponse
import reehi.board.articleread.service.response.ArticleReadResponse
import kotlin.test.Test


class ArticleReadApiTest {
    val articleReadRestClient: RestClient = RestClient.create("http://localhost:9005")
    val articleRestClient: RestClient = RestClient.create("http://localhost:9000")

    @Test
    fun readTest() {
        val response: ArticleReadResponse? = articleReadRestClient.get()
            .uri("/v1/articles/{articleId}", 158976973446656000L)
            .retrieve()
            .body(ArticleReadResponse::class.java)

        println("response = $response")
    }


    @Test
    fun readAllTest() {
        val response1 = articleReadRestClient.get()
            .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".format(1L, 3000L, 5))
            .retrieve()
            .body(ArticleReadPageResponse::class.java)

        System.out.println("response1.getArticleCount() = " + response1?.articleCount)
        for (article in response1?.articles!!) {
            println("article.getArticleId() = " + article.articleId)
        }

        val response2 = articleRestClient.get()
            .uri("/v1/articles?boardId=%s&page=%s&pageSize=%s".format(1L, 3000L, 5))
            .retrieve()
            .body(ArticleReadPageResponse::class.java)

        System.out.println("response2.getArticleCount() = " + response2?.articleCount)
        for (article in response2?.articles!!) {
            println("article.getArticleId() = " + article.articleId)
        }
    }

    @Test
    fun readAllInfiniteScrollTest() {
        val response1: List<ArticleReadResponse> = articleReadRestClient.get()
            .uri(
                "/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s".format(
                    1L,
                    5L,
                    126170915769933824L
                )
            )
            .retrieve()
            .body<List<ArticleReadResponse>?>(object : ParameterizedTypeReference<List<ArticleReadResponse>?>() {
            }) ?: emptyList()

        for (response in response1) {
            println("response = " + response.articleId)
        }

        val response2: List<ArticleReadResponse> = articleRestClient.get()
            .uri(
                "/v1/articles/infinite-scroll?boardId=%s&pageSize=%s&lastArticleId=%s".formatted(
                    1L,
                    5L,
                    126170915769933824L
                )
            )
            .retrieve()
            .body<List<ArticleReadResponse>?>(object : ParameterizedTypeReference<List<ArticleReadResponse>?>() {
            }) ?: emptyList()

        for (response in response2) {
            println("response = " + response.articleId)
        }
    }

}