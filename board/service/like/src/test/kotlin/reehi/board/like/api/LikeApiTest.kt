package reehi.board.like.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.RestClient
import reehi.board.like.service.response.ArticleLikeResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.test.Test


class LikeApiTest {
    val restClient = RestClient.create("http://localhost:9002")

    @Test
    fun likeAndUnlikeTest() {
        val articleId:Long = 9999L;

        like(articleId,1L,"pessimistic-lock-1")
        like(articleId,2L,"pessimistic-lock-1")
        like(articleId,3L,"optimistic-lock-1")

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

    fun like(articleId: Long, userId: Long, lockType: String) {
        restClient.post()
        .uri("/v1/article-likes/articles/$articleId/users/$userId/$lockType")
        .retrieve()
        .onStatus(
            { obj: HttpStatusCode -> obj.is5xxServerError },
            { request: HttpRequest?, response: ClientHttpResponse? -> })
        .toBodilessEntity();
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

    @Test
    fun likePerformanceTest() {
        val executorService = Executors.newFixedThreadPool(100)
        likePerformanceTest(executorService, 1111L, "pessimistic-lock-1")
        likePerformanceTest(executorService, 2222L, "pessimistic-lock-2")
        likePerformanceTest(executorService, 3333L, "optimistic-lock")
    }

    fun likePerformanceTest(executorService: ExecutorService, articleId: Long, lockType: String) {
        val latch = CountDownLatch(3000)
        println("$lockType start")

        like(articleId, 1L, lockType)

        val start = System.nanoTime()
        for (i in 0..2999) {
            val userId = (i + 2).toLong()
            executorService.submit {
                like(articleId, userId, lockType)
                latch.countDown()
            }
        }

        latch.await()

        val end = System.nanoTime()

        println("lockType = " + lockType + ", time = " + (end - start) / 1000000 + "ms")
        println("$lockType end")

        val count = restClient.get()
            .uri("/v1/article-likes/articles/{articleId}/count", articleId)
            .retrieve()
            .body(Long::class.java)

        println("count = $count")
    }

}