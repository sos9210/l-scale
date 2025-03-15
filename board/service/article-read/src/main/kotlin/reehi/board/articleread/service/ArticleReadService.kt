package reehi.board.articleread.service

import org.springframework.stereotype.Component
import reehi.board.articleread.client.ArticleClient
import reehi.board.articleread.client.CommentClient
import reehi.board.articleread.client.LikeClient
import reehi.board.articleread.client.ViewClient
import reehi.board.articleread.repository.ArticleIdListRepository
import reehi.board.articleread.repository.ArticleQueryModel
import reehi.board.articleread.repository.ArticleQueryModelRepository
import reehi.board.articleread.repository.BoardArticleCountRepository
import reehi.board.articleread.service.event.handler.EventHandler
import reehi.board.articleread.service.response.ArticleReadPageResponse
import reehi.board.articleread.service.response.ArticleReadResponse
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import java.time.Duration

@Component
class ArticleReadService (
    val articleClient: ArticleClient,
    val commentClient: CommentClient,
    val likeClient: LikeClient,
    val viewClient: ViewClient,
    val articleIdListRepository: ArticleIdListRepository,
    val articleQueryModelRepository: ArticleQueryModelRepository,
    val boardArticleCountRepository: BoardArticleCountRepository,
    val eventHandlers: List<EventHandler<EventPayload>>
){

    fun handleEvent(event: Event<EventPayload>) {
        eventHandlers.forEach { handler ->
            if (handler.supports(event)) {
                handler.handle(event)
            }
        }
    }

    fun read(articleId: Long): ArticleReadResponse? {
        var articleQueryModel = articleQueryModelRepository.read(articleId)
            if(articleQueryModel == null) {
                articleQueryModel = fetch(articleId)
            } else {
                throw IllegalArgumentException()
            }

        return viewClient.count(articleId).let {
            ArticleReadResponse.from(
                articleQueryModel!!,
                it!!
            )
        }
    }

    private fun fetch(articleId: Long): ArticleQueryModel? {
        val articleQueryModelOptional = articleClient.read(articleId)?.let {
                ArticleQueryModel.create(
                    it,
                    commentClient.count(it.articleId),
                    likeClient.count(it.articleId)
                )
            }
        articleQueryModelOptional
            ?.let {
                articleQueryModelRepository.create(it, Duration.ofDays(1))
            }
        println("[ArticleReadService.fetch] fetch data. articleId=$articleId, isPresent=${articleQueryModelOptional}")
        return articleQueryModelOptional
    }

    fun readAll(boardId: Long, page: Long, pageSize: Long): ArticleReadPageResponse {
        return ArticleReadPageResponse.of(
            readAll(
                readAllArticleIds(boardId, page, pageSize)
            ),
            count(boardId)
        )
    }

    private fun readAll(articleIds: List<Long>?): List<ArticleReadResponse> {
        val articleQueryModelMap = articleQueryModelRepository.readAll(articleIds)
        return articleIds?.mapNotNull { articleId ->
            if (articleQueryModelMap.containsKey(articleId)) {
                articleQueryModelMap[articleId]
            } else {
                fetch(articleId)
            }
        }!!.map { articleQueryModel ->
            ArticleReadResponse.from(
                articleQueryModel,
                viewClient.count(articleQueryModel.articleId) ?: 0L
            )
        }
    }

    private fun readAllArticleIds(boardId: Long, page: Long, pageSize: Long): List<Long> {
        val articleIds = articleIdListRepository.readAll(boardId, (page - 1) * pageSize, pageSize)
        return if (pageSize == articleIds.size.toLong()) {
            println("[ArticleReadService.readAllArticleIds] return redis data.")
            articleIds
        } else {
            println("[ArticleReadService.readAllArticleIds] return origin data.")
            articleClient.readAll(boardId, page, pageSize)?.articles!!.map { it.articleId }
        }
    }

    private fun count(boardId: Long): Long {
        val result = boardArticleCountRepository.read(boardId)
        if (result != 0L) {
            return result
        }
        val count = articleClient.count(boardId) ?: 0L
        boardArticleCountRepository.createOrUpdate(boardId, count)
        return count
    }

    fun readAllInfiniteScroll(boardId: Long, lastArticleId: Long?, pageSize: Long): List<ArticleReadResponse> {
        return readAll(
            readAllInfiniteScrollArticleIds(boardId, lastArticleId, pageSize)
        )
    }

    private fun readAllInfiniteScrollArticleIds(boardId: Long, lastArticleId: Long?, pageSize: Long): List<Long>? {
        val articleIds = articleIdListRepository.readAllInfiniteScroll(boardId, lastArticleId, pageSize)
        return if (pageSize == articleIds.size.toLong()) {
            println("[ArticleReadService.readAllInfiniteScrollArticleIds] return redis data.")
            articleIds
        } else {
            println("[ArticleReadService.readAllInfiniteScrollArticleIds] return origin data.")
            articleClient.readAllInfiniteScroll(boardId, lastArticleId, pageSize)?.map { it.articleId }
        }
    }
}