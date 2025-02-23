package reehi.board.like.controller

import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reehi.board.like.service.ArticleLikeService
import reehi.board.like.service.response.ArticleLikeResponse

@RestController
class ArticleLikeController(
    val articleLikeService: ArticleLikeService
) {

    @GetMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun read(
        @PathVariable articleId: Long,
        @PathVariable userId: Long
    ): ArticleLikeResponse =
        articleLikeService.read(articleId,userId)

    @PostMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun like (
        @PathVariable articleId: Long,
        @PathVariable userId: Long,
    ) {
        articleLikeService.like(articleId,userId)
    }

    @DeleteMapping("/v1/article-likes/articles/{articleId}/users/{userId}")
    fun unlike (
        @PathVariable articleId: Long,
        @PathVariable userId: Long,
    ) {
        articleLikeService.unlike(articleId,userId)
    }
}