package reehi.board.comment.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import reehi.board.comment.service.response.CommentPageResponse
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
    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v1/comments?articleId=1&page=50000&pageSize=10")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.commentCount ${response?.commentCount}")

        for (comment in response?.comments!!) {
            if(comment.commentId != comment.parentCommentId) {
                print("\t")
            }
            println("commentId : ${comment.commentId}")
        }
    }

    @Test
    fun readAllInfiniteScroll() {
        val response1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("firstPage")
        for (comment in response1!!) {
            if(comment.commentId != comment.parentCommentId) {
                print("\t")
            }
            println("commentId : ${comment.commentId}")
        }

        val lastParentCommentId = response1.last().parentCommentId;
        val lastCommentId = response1.last().commentId;

        val response2 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=$lastParentCommentId&lastCommentId=$lastCommentId")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("secondPage")
        for (comment in response2!!) {
            if(comment.commentId != comment.parentCommentId) {
                print("\t")
            }
            println("commentId : ${comment.commentId}")
        }
    }
    class CommentCreateRequest (
        val articleId: Long,
        val content: String,
        val parentCommentId: Long?,
        val writerId: Long,
    ) {
    }
}