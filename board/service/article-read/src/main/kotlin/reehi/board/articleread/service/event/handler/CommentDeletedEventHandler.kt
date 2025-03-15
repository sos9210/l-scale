package reehi.board.articleread.service.event.handler

import org.springframework.stereotype.Component
import reehi.board.articleread.repository.ArticleQueryModelRepository
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.CommentCreatedEventPayload
import reehi.board.common.event.payload.CommentDeletedEventPayload


@Component
class CommentDeletedEventHandler (
    val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<CommentDeletedEventPayload> {

    override fun handle(event: Event<CommentDeletedEventPayload>) {
        articleQueryModelRepository.read(event.payload.articleId)
            ?.let {
                it.updateBy(event.payload)
                articleQueryModelRepository.update(it)

            }
    }

    override fun supports(event: Event<CommentDeletedEventPayload>): Boolean {
        return EventType.COMMENT_DELETED === event.type
    }

}