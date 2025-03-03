package reehi.board.hotarticle.client

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.time.LocalDateTime


@Component
class ArticleClient (
){
    private lateinit var restClient: RestClient

    @Value("\${endpoints.reehi-board-article-service.url}")
    private val articleServiceUrl: String? = null

    @PostConstruct
    fun initRestClient() {
        restClient = RestClient.create(articleServiceUrl!!)
    }

    fun read(articleId: Long): ArticleResponse? {
        try {
            return restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse::class.java)
        } catch (e: Exception) {
            println("[ArticleClient.read] articleId=$articleId")
        }
        return null
    }

    companion object {
        class ArticleResponse (
            val articleId: Long,
            val title: String,
            val createdAt: LocalDateTime

        )


    }

}