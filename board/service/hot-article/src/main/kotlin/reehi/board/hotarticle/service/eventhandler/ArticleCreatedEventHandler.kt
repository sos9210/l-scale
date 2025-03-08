package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleCreatedEventPayload
import reehi.board.hotarticle.repository.ArticleCreatedTimeRepository
import reehi.board.hotarticle.utils.TimeCalculatorUtils


@Component
class ArticleCreatedEventHandler(
    val articleCreatedTimeRepository : ArticleCreatedTimeRepository
): EventHandler<ArticleCreatedEventPayload> {

    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload: ArticleCreatedEventPayload = event.payload
        articleCreatedTimeRepository.createOrUpdate(
            payload.articleId,
            payload.createdAt,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean {
        return EventType.ARTICLE_CREATED === event.type
    }

    override fun findArticleId(event: Event<ArticleCreatedEventPayload>): Long {
        return event.payload.articleId
    }

}