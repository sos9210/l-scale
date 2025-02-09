package reehi.board.article.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.article.entity.Article
import reehi.board.article.repository.ArticleRepository
import reehi.board.article.service.request.ArticleCreateRequest
import reehi.board.article.service.request.ArticleUpdateRequest
import reehi.board.article.service.response.ArticleResponse

@Service
class ArticleService (
    val articleRepository: ArticleRepository
){
    val snowflake: Snowflake = Snowflake();


    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article.create(snowflake.nextId(), request.title, request.content, request.boardId, request.writerId)
        )
        return ArticleResponse.from(article)
    }

    @Transactional
    fun update(articleId: Long, request: ArticleUpdateRequest): ArticleResponse {
        val article = articleRepository.findById(articleId).orElseThrow()
        article.update(request.title,request.content)
        return ArticleResponse.from(article)
    }

    fun read(articleId: Long): ArticleResponse =
        ArticleResponse.from(articleRepository.findById(articleId).orElseThrow())

    @Transactional
    fun delete(articleId: Long) {
        articleRepository.deleteById(articleId)
    }

}