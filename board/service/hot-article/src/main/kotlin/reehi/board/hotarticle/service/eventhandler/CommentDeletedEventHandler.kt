package reehi.board.hotarticle.service.eventhandler

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.CommentDeletedEventPayload
import reehi.board.hotarticle.repository.ArticleCommentCountRepository
import reehi.board.hotarticle.utils.TimeCalculatorUtils

@Component
class CommentDeletedEventHandler(
    val articleCommentCountRepository: ArticleCommentCountRepository

) : EventHandler<CommentDeletedEventPayload> {

    override fun handle(event: Event<CommentDeletedEventPayload>) {
        val payload: CommentDeletedEventPayload = event.payload
        articleCommentCountRepository.createOrUpdate(
            payload.articleId,
            payload.articleCommentCount,
            TimeCalculatorUtils.calculateDurationToMidnight()
        )
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.COMMENT_DELETED === event.type
    }

    override fun findArticleId(event: Event<CommentDeletedEventPayload>): Long {
        return event.payload.articleId
    }
}