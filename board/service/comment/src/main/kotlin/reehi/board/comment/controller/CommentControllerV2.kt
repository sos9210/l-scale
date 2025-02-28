package reehi.board.comment.controller

import org.springframework.web.bind.annotation.*
import reehi.board.comment.service.CommentServiceV2
import reehi.board.comment.service.request.CommentCreateRequestV2
import reehi.board.comment.service.response.CommentPageResponse
import reehi.board.comment.service.response.CommentResponse


@RestController
class CommentControllerV2 (
    val commentService: CommentServiceV2
){

    @GetMapping("/v2/comments/{commentId}")
    fun read(@PathVariable("commentId") commentId: Long) : CommentResponse =
        commentService.read(commentId)

    @PostMapping("/v2/comments")
    fun read(@RequestBody request: CommentCreateRequestV2) : CommentResponse =
        commentService.create(request)

    @DeleteMapping("/v2/comments/{commentId}")
    fun delete(@PathVariable("commentId") commentId: Long) =
        commentService.delete(commentId)

    @GetMapping("/v2/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long
    ): CommentPageResponse {
        return commentService.readAll(articleId, page, pageSize)
    }

    @GetMapping("/v2/comments/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastPath", required = false) lastPath: String?,
        @RequestParam("pageSize") pageSize: Long
    ): List<CommentResponse> {
        return commentService.readAllInfiniteScroll(articleId, lastPath, pageSize)
    }

    @GetMapping("/v2/comments/articles/{articleId}/count")
    fun count(@PathVariable("articleId") articleId: Long): Long =
        commentService.count(articleId)

}