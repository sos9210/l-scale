package reehi.board.like.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import reehi.board.like.service.response.ArticleLikeResponse
import kotlin.test.Test

class LikeApiTest {
    val restClient = RestClient.create("http://localhost:9002")

    @Test
    fun likeAndUnlikeTest() {
        val articleId:Long = 9999L;

        like(articleId,1L);
        like(articleId,2L);
        like(articleId,3L);

        val response1 = read(articleId, 1L);
        val response2 = read(articleId,2L);
        val response3 = read(articleId,3L);
        println(response1?.articleLikeId)
        println(response2?.articleLikeId)
        println(response3?.articleLikeId)

        unlike(articleId,1L)
        unlike(articleId,2L)
        unlike(articleId,3L)
    }

    fun like(articleId: Long, userId: Long) {
        restClient.post()
        .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
            .body(object : ParameterizedTypeReference<ArticleLikeResponse>() {})
    }

    fun unlike(articleId: Long, userId: Long) {
        restClient.delete()
        .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
            .body(object : ParameterizedTypeReference<ArticleLikeResponse>() {})
    }

    fun read(articleId: Long, userId: Long) : ArticleLikeResponse? {
        return restClient.get()
            .uri("/v1/article-likes/articles/$articleId/users/$userId")
            .retrieve()
            .body(ArticleLikeResponse::class.java)
    }
}