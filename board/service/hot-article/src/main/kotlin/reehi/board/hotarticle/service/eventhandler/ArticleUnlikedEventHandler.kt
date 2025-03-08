package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleUnlikedEventPayload
import reehi.board.hotarticle.repository.ArticleLikeCountRepository
import reehi.board.hotarticle.utils.TimeCalculatorUtils

@Component
class ArticleUnlikedEventHandler(
    val articleLikeCountRepository: ArticleLikeCountRepository

) : EventHandler<ArticleUnlikedEventPayload> {

    override fun handle(event: Event<ArticleUnlikedEventPayload>) {
        val payload: ArticleUnlikedEventPayload = event.payload
        articleLikeCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleLikeCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<ArticleUnlikedEventPayload>): Boolean {
        return EventType.ARTICLE_UNLIKED === event.type
    }

    override fun findArticleId(event: Event<ArticleUnlikedEventPayload>): Long {
        return event.payload.articleId
    }
}