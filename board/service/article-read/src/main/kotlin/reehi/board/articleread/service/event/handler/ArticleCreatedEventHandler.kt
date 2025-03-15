package reehi.board.articleread.service.event.handler

import org.springframework.stereotype.Component
import reehi.board.articleread.repository.ArticleIdListRepository
import reehi.board.articleread.repository.ArticleQueryModel
import reehi.board.articleread.repository.ArticleQueryModelRepository
import reehi.board.articleread.repository.BoardArticleCountRepository
import reehi.board.common.event.Event
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleCreatedEventPayload
import java.time.Duration


@Component
class ArticleCreatedEventHandler (
    val articleQueryModelRepository: ArticleQueryModelRepository,
    val articleIdListRepository: ArticleIdListRepository,
    val boardArticleCountRepository: BoardArticleCountRepository
): EventHandler<ArticleCreatedEventPayload> {
    override fun handle(event: Event<ArticleCreatedEventPayload>) {
        val payload: ArticleCreatedEventPayload = event.payload
        articleQueryModelRepository.create(
            ArticleQueryModel.create(payload),
            Duration.ofDays(1)
        )
        articleIdListRepository.add(payload.boardId, payload.articleId, 1000L)
        boardArticleCountRepository.createOrUpdate(payload.boardId, payload.boardArticleCount)
    }

    override fun supports(event: Event<ArticleCreatedEventPayload>): Boolean {
        return EventType.ARTICLE_CREATED === event.type
    }

}