package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleViewedEventPayload
import reehi.board.hotarticle.repository.ArticleViewCountRepository
import reehi.board.hotarticle.utils.TimeCalculatorUtils

@Component
class ArticleViewedEventHandler(
    val articleViewCountRepository: ArticleViewCountRepository

) : EventHandler<ArticleViewedEventPayload> {

    override fun handle(event: Event<ArticleViewedEventPayload>) {
        val payload: ArticleViewedEventPayload = event.payload
        articleViewCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleViewCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleViewedEventPayload>): Boolean {
        return EventType.ARTICLE_VIEWED === event.type
    }

    override fun findArticleId(event: Event<ArticleViewedEventPayload>): Long {
        return event.payload.articleId
    }
}