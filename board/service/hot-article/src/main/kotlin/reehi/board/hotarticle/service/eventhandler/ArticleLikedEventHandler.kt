package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleLikedEventPayload
import reehi.board.hotarticle.repository.ArticleLikeCountRepository
import reehi.board.hotarticle.utils.TimeCalculatorUtils

@Component
class ArticleLikedEventHandler(
    val articleLikeCountRepository: ArticleLikeCountRepository
) : EventHandler<ArticleLikedEventPayload> {

    override fun handle(event: Event<ArticleLikedEventPayload>) {
        val payload: ArticleLikedEventPayload = event.payload
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean {
        return EventType.ARTICLE_LIKED === event.type
    }

    override fun findArticleId(event: Event<ArticleLikedEventPayload>): Long {
        return event.payload.articleId
    }
}