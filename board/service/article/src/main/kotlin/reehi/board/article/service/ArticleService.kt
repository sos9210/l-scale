package reehi.board.article.service

import kuke.board.common.snowflake.Snowflake
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reehi.board.article.entity.Article
import reehi.board.article.entity.BoardArticleCount
import reehi.board.article.repository.ArticleRepository
import reehi.board.article.repository.BoardArticleCountRepository
import reehi.board.article.service.request.ArticleCreateRequest
import reehi.board.article.service.request.ArticleUpdateRequest
import reehi.board.article.service.response.ArticlePageResponse
import reehi.board.article.service.response.ArticleResponse

@Service
class ArticleService (
    val articleRepository: ArticleRepository,
    val boardArticleCountRepository: BoardArticleCountRepository,
){
    val snowflake: Snowflake = Snowflake();


    @Transactional
    fun create(request: ArticleCreateRequest): ArticleResponse {
        val article = articleRepository.save(
            Article.create(snowflake.nextId(), request.title, request.content, request.boardId, request.writerId)
        )
        val result = boardArticleCountRepository.increase(request.boardId)
        if(result == 0) {
            boardArticleCountRepository.save(
                BoardArticleCount.init(request.boardId, 1L)
            )
        }
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
        val article = articleRepository.findByIdOrNull(articleId)
        article ?: throw NoSuchElementException("Article not found")
        articleRepository.delete(article)
        boardArticleCountRepository.decrease(article.boardId)
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticlePageResponse =
        ArticlePageResponse.of(
            articleRepository.findAll(boardId, (page - 1) * pageSize, pageSize)
                .map { ArticleResponse.from(it)}.toList(),
            articleRepository.count(
                boardId,
                PageLimitCalculator.calculatePageLimit(page,pageSize, 10L)
            )

        )

    fun readAllInfiniteScroll(boardId: Long, pageSize: Long, lastArticleId: Long?) :List<ArticleResponse> {
        val articles =
        if (lastArticleId == null) articleRepository.findAllInfiniteScroll(boardId, pageSize)
        else articleRepository.findAllInfiniteScroll(boardId, pageSize, lastArticleId)

        return articles.map {ArticleResponse.from(it) }.toList()
    }

    fun count(boardId: Long): Long = boardArticleCountRepository.findByIdOrNull(boardId)?.articleCount ?: 0L

}