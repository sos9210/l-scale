package reehi.board.hotarticle.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reehi.board.hotarticle.service.HotArticleService
import reehi.board.hotarticle.service.response.HotArticleResponse


@RestController
class HotArticleController (
    val hotArticleService: HotArticleService

){
    @GetMapping("/v1/hot-articles/articles/date/{dateStr}")
    fun readAll(
        @PathVariable("dateStr") dateStr: String
    ): List<HotArticleResponse> {
        return hotArticleService.readAll(dateStr)
    }
}