package reehi.board.articleread.service.event.handler

import org.springframework.stereotype.Component
import reehi.board.articleread.repository.ArticleIdListRepository
import reehi.board.articleread.repository.ArticleQueryModel
import reehi.board.articleread.repository.ArticleQueryModelRepository
import reehi.board.articleread.repository.BoardArticleCountRepository
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleCreatedEventPayload
import reehi.board.common.event.payload.ArticleDeletedEventPayload
import reehi.board.common.event.payload.ArticleUpdatedEventPayload
import java.time.Duration


@Component
class ArticleDeletedEventHandler (
    val articleQueryModelRepository: ArticleQueryModelRepository,
    val articleIdListRepository: ArticleIdListRepository,
    val boardArticleCountRepository: BoardArticleCountRepository
): EventHandler<ArticleDeletedEventPayload> {
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload = event.payload
        articleIdListRepository.delete(payload.boardId,payload.articleId)
        articleQueryModelRepository.delete(payload.articleId)
        boardArticleCountRepository.createOrUpdate(payload.boardId,payload.articleId)

    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean {
        return EventType.ARTICLE_DELETED === event.type
    }

}