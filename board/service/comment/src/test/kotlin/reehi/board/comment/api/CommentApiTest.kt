package reehi.board.comment.api

import org.springframework.web.client.RestClient
import reehi.board.comment.service.response.CommentResponse
import kotlin.test.Test

class CommentApiTest {
    val restClient: RestClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = createComment(CommentCreateRequest(1L, "my comment1", null, 1L))
        val response2 = createComment(CommentCreateRequest(1L, "my comment2", response1?.commentId, 1L))
        val response3 = createComment(CommentCreateRequest(1L, "my comment3", response1?.commentId, 1L))

        println("commentId = ${response1?.commentId}")
        println("commentId = ${response2?.commentId}")
        println("commentId = ${response3?.commentId}")

    }

    fun createComment(request: CommentCreateRequest) : CommentResponse? {
        return restClient.post().uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get().uri("/v1/comments/{commentId}", 150626814784303104L)
            .retrieve()
            .body(CommentResponse::class.java)

        println("response = ${response?.commentId}")
    }
    @Test
    fun delete() {
        /**
         * commentId = 150626814784303104
         * commentId = 150626815447003136
         * commentId = 150626815505723392
         */
        restClient.delete()
            .uri("/v1/comments/{commentId}", 150626815505723392)
            .retrieve().toEntity(CommentResponse::class.java)

    }

    class CommentCreateRequest (
        val articleId: Long,
        val content: String,
        val parentCommentId: Long?,
        val writerId: Long,
    ) {
    }
}