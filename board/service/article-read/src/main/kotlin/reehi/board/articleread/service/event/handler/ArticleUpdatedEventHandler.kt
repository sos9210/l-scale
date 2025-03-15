package reehi.board.articleread.service.event.handler

import org.springframework.stereotype.Component
import reehi.board.articleread.repository.ArticleQueryModel
import reehi.board.articleread.repository.ArticleQueryModelRepository
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleCreatedEventPayload
import reehi.board.common.event.payload.ArticleUpdatedEventPayload
import java.time.Duration


@Component
class ArticleUpdatedEventHandler (
    val articleQueryModelRepository: ArticleQueryModelRepository
): EventHandler<ArticleUpdatedEventPayload> {
    override fun handle(event: Event<ArticleUpdatedEventPayload>) {
        articleQueryModelRepository.read(event.payload.articleId)
            ?.let {
                it.updateBy(event.payload)
                articleQueryModelRepository.update(it)

            }

//        articleIdListRepository.add(payload.boardId, payload.articleId, 1000L)
//        boardArticleCountRepository.createOrUpdate(payload.boardId, payload.boardArticleCount)
    }

    override fun supports(event: Event<ArticleUpdatedEventPayload>): Boolean {
        return EventType.ARTICLE_UPDATED === event.type
    }

}