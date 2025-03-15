package reehi.board.articleread.client

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class LikeClient(
    @Value("\${endpoints.reehi-board-like-service.url}")
    val likeServiceUrl: String
) {
    private lateinit var restClient: RestClient


    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(likeServiceUrl)
    }

    fun count(articleId: Long): Long?{
        try {
            return restClient.get()
                .uri("/v1/article-likes/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java)
        } catch (e: Exception) {
            println("[LikeClient.count] articleId=$articleId")
            println(e)
            return 0
        }
    }

}