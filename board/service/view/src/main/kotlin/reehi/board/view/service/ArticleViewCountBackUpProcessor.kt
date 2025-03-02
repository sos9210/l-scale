package reehi.board.view.service

import org.hibernate.event.spi.EventType
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reehi.board.view.entity.ArticleViewCount
import reehi.board.view.repository.ArticleViewCountBackUpRepository


@Component
class ArticleViewCountBackUpProcessor (
  //  val outboxEventPublisher: OutboxEventPublisher,
    val articleViewCountBackUpRepository: ArticleViewCountBackUpRepository
){

    @Transactional
    fun backUp(articleId: Long, viewCount: Long?) {
        val result = articleViewCountBackUpRepository.updateViewCount(articleId, viewCount!!)
        if (result == 0) {
            articleViewCountBackUpRepository.findByIdOrNull(articleId) ?:
                articleViewCountBackUpRepository.save(ArticleViewCount.init(articleId, viewCount))

        }

//        outboxEventPublisher.publish(
//            EventType.ARTICLE_VIEWED,
//            ArticleViewedEventPayload.builder()
//                .articleId(articleId)
//                .articleViewCount(viewCount)
//                .build(),
//            articleId
//        )
    }
}