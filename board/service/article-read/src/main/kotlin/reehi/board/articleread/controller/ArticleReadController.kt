package reehi.board.articleread.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reehi.board.articleread.service.ArticleReadService
import reehi.board.articleread.service.response.ArticleReadPageResponse
import reehi.board.articleread.service.response.ArticleReadResponse


@RestController
class ArticleReadController (
    val articleReadService: ArticleReadService
){
    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable("articleId") articleId: Long): ArticleReadResponse? {
        return articleReadService.read(articleId)
    }

    @GetMapping("/v1/articles")
    fun readAll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam("page") page: Long,
        @RequestParam("pageSize") pageSize: Long
    ): ArticleReadPageResponse {
        return articleReadService.readAll(boardId, page, pageSize)
    }

    @GetMapping("/v1/articles/infinite-scroll")
    fun readAllInfiniteScroll(
        @RequestParam("boardId") boardId: Long,
        @RequestParam(value = "lastArticleId", required = false) lastArticleId: Long?,
        @RequestParam("pageSize") pageSize: Long
    ): List<ArticleReadResponse> {
        return articleReadService.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
    }


}