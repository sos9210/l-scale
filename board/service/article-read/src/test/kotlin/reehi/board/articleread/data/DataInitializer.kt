package reehi.board.articleread.data

import org.springframework.web.client.RestClient
import java.util.random.RandomGenerator
import kotlin.test.Test


class DataInitializer {
    var articleServiceClient: RestClient = RestClient.create("http://localhost:9000")
    var commentServiceClient: RestClient = RestClient.create("http://localhost:9001")
    var likeServiceClient: RestClient = RestClient.create("http://localhost:9002")
    var viewServiceClient: RestClient = RestClient.create("http://localhost:9003")

    @Test
    fun initialize() {
        for (i in 0..29) {
            val articleId = createArticle()
            println("articleId : $articleId")
            val commentCount = RandomGenerator.getDefault().nextLong(10)
            val likeCount = RandomGenerator.getDefault().nextLong(10)
            val viewCount = RandomGenerator.getDefault().nextLong(200)

            createComment(articleId, commentCount)
            like(articleId, likeCount)
            view(articleId, viewCount)
        }
    }

    fun createArticle(): Long {
        return articleServiceClient.post()
            .uri("/v1/articles")
            .body(ArticleCreateRequest("title", "content", 1L, 1L))
            .retrieve()
            .body(ArticleResponse::class.java)
            ?.articleId ?: 0L
    }




    fun createComment(articleId: Long, commentCount: Long) {
        var commentCount = commentCount
        while (commentCount-- > 0) {
            commentServiceClient.post()
                .uri("/v2/comments")
                .body(CommentCreateRequest(articleId, "content", 1L))
                .retrieve()
        }
    }
    companion object {
        class CommentCreateRequest (
            val articleId: Long,
            val content: String,
            val writerId: Long

        )

        class ArticleResponse (
            val articleId: Long

        )

        class ArticleCreateRequest (
            val title: String,
            val content: String,
            val writerId: Long,
            val boardId: Long

        )
    }

    fun like(articleId: Long?, likeCount: Long) {
        var likeCount = likeCount
        while (likeCount-- > 0) {
            likeServiceClient.post()
                .uri("/v1/article-likes/articles/{articleId}/users/{userId}/pessimistic-lock-1", articleId, likeCount)
                .retrieve()
        }
    }

    fun view(articleId: Long?, viewCount: Long) {
        var viewCount = viewCount
        while (viewCount-- > 0) {
            viewServiceClient.post()
                .uri("/v1/article-views/articles/{articleId}/users/{userId}", articleId, viewCount)
                .retrieve()
        }
    }

}