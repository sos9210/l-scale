package reehi.board.articleread.service.event.handler

import org.springframework.stereotype.Component
import reehi.board.articleread.repository.ArticleQueryModelRepository
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleLikedEventPayload


@Component
class ArticleLikedEventHandler (
    val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<ArticleLikedEventPayload> {

    override fun handle(event: Event<ArticleLikedEventPayload>) {
        articleQueryModelRepository.read(event.payload.articleId)
            ?.let {
                it.updateBy(event.payload)
                articleQueryModelRepository.update(it)

            }
    }

    override fun supports(event: Event<ArticleLikedEventPayload>): Boolean {
        return EventType.ARTICLE_LIKED === event.type
    }

}