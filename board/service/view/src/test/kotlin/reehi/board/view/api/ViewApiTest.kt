package reehi.board.view.api

import org.springframework.web.client.RestClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.test.Test


class ViewApiTest {
    val restClient = RestClient.create("http://localhost:9003")

    @Test
    @Throws(InterruptedException::class)
    fun viewTest() {
        val executorService = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)

        for (i in 0..9999) {
            executorService.submit {
                restClient.post()
                    .uri("/v1/article-views/articles/{articleId}/users/{userId}", 4L, 1L)
                    .retrieve()
                    .toBodilessEntity()
                latch.countDown()
            }
        }

        latch.await()

        val count = restClient.get()
            .uri("/v1/article-views/articles/{articleId}/count", 4L)
            .retrieve()
            .body(Long::class.java)

        println("count = $count")
    }

}