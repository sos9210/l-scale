package reehi.board.articleread.client

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import reehi.board.articleread.cache.OptimizedCacheable

@Component
class ViewClient(
    @Value("\${endpoints.reehi-board-view-service.url}")
    val viewServiceUrl: String,
) {
    val restClient: RestClient = RestClient.create(viewServiceUrl)



//    @Cacheable(key = "#articleId", value = arrayOf("articleViewCount"))
    @OptimizedCacheable(type = "articleViewCount", ttlSeconds = 1)
    fun count(articleId: Long): Long? {
        println("[ViewClient.count] articleId=$articleId")
        try {
            return restClient.get()
                .uri("/v1/article-views/articles/{articleId}/count", articleId)
                .retrieve()
                .body(Long::class.java)
        } catch (e: Exception) {
            println("[ViewClient.count] articleId=$articleId")
            println(e)
            return 0
        }
    }

}