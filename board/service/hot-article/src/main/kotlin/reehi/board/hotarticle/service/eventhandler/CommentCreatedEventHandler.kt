package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.CommentCreatedEventPayload
import reehi.board.hotarticle.repository.ArticleCommentCountRepository
import reehi.board.hotarticle.utils.TimeCalculatorUtils

@Component
class CommentCreatedEventHandler(
     val articleCommentCountRepository: ArticleCommentCountRepository
) : EventHandler<CommentCreatedEventPayload> {

    override fun handle(event: Event<CommentCreatedEventPayload>) {
        val payload: CommentCreatedEventPayload = event.payload
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<CommentCreatedEventPayload>): Boolean {
        return EventType.COMMENT_CREATED === event.type
    }

    override fun findArticleId(event: Event<CommentCreatedEventPayload>): Long {
        return event.payload.articleId
    }
}