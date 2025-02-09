package reehi.board.article.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reehi.board.article.service.ArticleService
import reehi.board.article.service.request.ArticleCreateRequest
import reehi.board.article.service.request.ArticleUpdateRequest
import reehi.board.article.service.response.ArticleResponse

@RestController
class ArticleController(
    val articleService: ArticleService
) {

    @GetMapping("/v1/articles/{articleId}")
    fun read(@PathVariable articleId: Long): ArticleResponse =
        articleService.read(articleId)


    @PostMapping("/v1/articles")
    fun create(@RequestBody request: ArticleCreateRequest): ArticleResponse =
        articleService.create(request)


    @PutMapping("/v1/articles/{articleId}")
    fun update(@PathVariable articleId: Long,@RequestBody request: ArticleUpdateRequest) =
        articleService.update(articleId,request)

    @PutMapping("/v1/articles/{articleId}")
    fun delete(@PathVariable articleId: Long) =
        articleService.delete(articleId)

}