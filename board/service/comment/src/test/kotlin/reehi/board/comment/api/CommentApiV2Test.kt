package reehi.board.comment.api

import org.springframework.core.ParameterizedTypeReference
import org.springframework.web.client.RestClient
import reehi.board.comment.service.response.CommentPageResponse
import reehi.board.comment.service.response.CommentResponse
import kotlin.test.Test

class CommentApiV2Test {
    val restClient: RestClient = RestClient.create("http://localhost:9001")

    @Test
    fun create() {
        val response1 = create(CommentCreateRequestV2(1L, "my comment1", null, 1L))
        val response2 = create(CommentCreateRequestV2(1L, "my comment2", response1?.path, 1L))
        val response3 = create(CommentCreateRequestV2(1L, "my comment3", response2?.path, 1L))

        println("response1.path = ${response1?.path}")
        println("response1.commentId = ${response1?.commentId}")
        println("\tresponse2.path = ${response2?.path}")
        println("\tresponse2.commentId = ${response2?.commentId}")
        println("\t\tresponse3.path = ${response3?.path}")
        println("\t\tresponse3.commentId = ${response3?.commentId}")
        /**
         * response1.path = 00002
         * response1.commentId = 152001811168600064
         * 	response2.path = 0000200000
         * 	response2.commentId = 152001811562864640
         * 		response3.path = 000020000000000
         * 		response3.commentId = 152001811625779200
         */
    }

    fun create(request: CommentCreateRequestV2) : CommentResponse? {
        return restClient.post().uri("/v2/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse::class.java)
    }

    @Test
    fun read() {
        val response = restClient.get().uri("/v2/comments/{commentId}", 152001811625779200L)
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
            .uri("/v2/comments/{commentId}", 152001811625779200)
            .retrieve().toEntity(CommentResponse::class.java)

    }
    @Test
    fun readAll() {
        val response = restClient.get()
            .uri("/v2/comments?articleId=1&pageSize=10&page=1")
            .retrieve()
            .body(CommentPageResponse::class.java)

        println("response.commentCount ${response?.commentCount}")

        for (comment in response?.comments!!) {
            println("commentId : ${comment.commentId}")
        }
        /**
         * 	commentId : 152001338189520896
         * 	commentId : 152001339204542464
         * 	commentId : 152001339263262720
         * 	commentId : 152001607392534528
         * 	commentId : 152001607711301632
         * 	commentId : 152001607770021888
         * 	commentId : 152001811168600064
         * 	commentId : 152001811562864640
         * 	commentId : 152005947333808132
         * 	commentId : 152005947430277121
         */
    }

    @Test
    fun readAllInfiniteScroll() {
        val response1 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("firstPage")
        for (comment in response1!!) {
            println("commentId : ${comment.commentId}")
        }

        val lastPath = response1.last().path;

        val response2 = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=5&lastPath=$lastPath")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<CommentResponse>>() {})

        println("secondPage")
        for (comment in response2!!) {
            println("commentId : ${comment.commentId}")
        }
    }

    class CommentCreateRequestV2 (
        val articleId: Long,
        val content: String,
        val parentPath: String?,
        val writerId: Long,
    ) {
    }
}