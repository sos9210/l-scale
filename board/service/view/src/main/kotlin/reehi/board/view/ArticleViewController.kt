package reehi.board.view

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reehi.board.view.service.ArticleViewService


@RestController
class ArticleViewController (
    val articleViewService: ArticleViewService
){

    @PostMapping("/v1/article-views/articles/{articleId}/users/{userId}")
    fun increase(
        @PathVariable("articleId") articleId: Long,
        @PathVariable("userId") userId: Long
    ): Long =
        articleViewService.increase(articleId, userId)

    @GetMapping("/v1/article-views/articles/{articleId}/count")
    fun count(@PathVariable("articleId") articleId: Long): Long =
        articleViewService.count(articleId)

}