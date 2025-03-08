package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleDeletedEventPayload
import reehi.board.hotarticle.repository.ArticleCreatedTimeRepository
import reehi.board.hotarticle.repository.HotArticleListRepository


@Component
class ArticleDeletedEventHandler(
    val hotArticleListRepository: HotArticleListRepository,
    val articleCreatedTimeRepository: ArticleCreatedTimeRepository
): EventHandler<ArticleDeletedEventPayload> {
    override fun handle(event: Event<ArticleDeletedEventPayload>) {
        val payload: ArticleDeletedEventPayload = event.payload
        articleCreatedTimeRepository.delete(payload.articleId)
        hotArticleListRepository.remove(payload.articleId, payload.createdAt)
    }

    override fun supports(event: Event<ArticleDeletedEventPayload>): Boolean {
        return EventType.ARTICLE_DELETED === event.type
    }

    override fun findArticleId(event: Event<ArticleDeletedEventPayload>): Long {
        return event.payload.articleId
    }
}