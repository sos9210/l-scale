package reehi.board.view.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reehi.board.common.event.EventType
import reehi.board.common.event.payload.ArticleViewedEventPayload
import reehi.board.common.outboxmessagerelay.OutboxEventPublisher
import reehi.board.view.entity.ArticleViewCount
import reehi.board.view.repository.ArticleViewCountBackUpRepository


@Component
class ArticleViewCountBackUpProcessor (
    val outboxEventPublisher: OutboxEventPublisher,
    val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository
){

    @Transactional
    fun backUp(articleId: Long, viewCount: Long?) {
        val result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount!!)
        if (result == 0) {
            articleViewCountBackUpRepository.findByIdOrNull(articleId) ?:
                articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount))

        }

        outboxEventPublisher.publish(
            EventType.ARTICLE_VIEWED,
            ArticleViewedEventPayload(
                articleId = articleId,
                articleViewCount = viewCount,

            ),
            articleId
        )
    }
}