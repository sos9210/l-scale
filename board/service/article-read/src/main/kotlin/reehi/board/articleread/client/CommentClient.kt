package reehi.board.articleread.client

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CommentClient(
    @Value("\${endpoints.reehi-board-comment-service.url}")
    val commentServiceUrl: String
) {
    private lateinit var restClient: RestClient


    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(commentServiceUrl)
    }

    fun count(articleId: Long): Long? {
        try {
            return restClient.get()
                .uri("/v2/comments/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java)
        } catch (e: Exception) {
            println("[CommentClient.count] articleId=$articleId")
            println(e)
            return 0
        }
    }

}