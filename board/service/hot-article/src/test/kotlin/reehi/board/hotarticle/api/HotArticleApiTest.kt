package reehi.board.hotarticle.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import reehi.board.hotarticle.service.response.HotArticleResponse
import kotlin.test.Test


class HotArticleApiTest {
    val restClient = RestClient.create("http://localhost:9004")

    @Test
    fun readAllTest() {
        val responses: List<HotArticleResponse> = restClient.get()
            .uri("/v1/hot-articles/articles/date/{dateStr}", "20250311")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<HotArticleResponse>>() {})
            ?: emptyList()

        responses.forEach { response ->
            println("response = $response")
        }
    }
}