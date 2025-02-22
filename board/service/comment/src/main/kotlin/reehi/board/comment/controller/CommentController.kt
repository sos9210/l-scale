package reehi.board.comment.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reehi.board.comment.service.CommentService
import reehi.board.comment.service.request.CommentCreateRequest
import reehi.board.comment.service.response.CommentPageResponse
import reehi.board.comment.service.response.CommentResponse

@RestController
class CommentController (
    val commentService: CommentService
){

    @GetMapping("/v1/comments/{commentId}")
    fun read(@PathVariable("commentId") commentId: Long) : CommentResponse =
        commentService.read(commentId)

    @PostMapping("/v1/comments")
    fun read(@RequestBody request: CommentCreateRequest) : CommentResponse =
        commentService.create(request)

    @DeleteMapping("/v1/comments/{commentId}")
    fun delete(@PathVariable("commentId") commentId: Long) =
        commentService.delete(commentId)

    @GetMapping("/v1/comments")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long,
    ) : CommentPageResponse =
        commentService.readAll(articleId,page,pageSize)

    @GetMapping("/v1/comments/infinite-scroll")
    fun readAll(
        @RequestParam("articleId") articleId: Long,
        @RequestParam(value = "lastParentCommentId", required = false) lastParentCommentId: Long?,
        @RequestParam(value = "lastCommentId", required = false) lastCommentId: Long?,
        @RequestParam("pageSize") pageSize: Long,
    ) : List<CommentResponse> =
        commentService.readAll(articleId,lastParentCommentId,lastCommentId,pageSize)


}