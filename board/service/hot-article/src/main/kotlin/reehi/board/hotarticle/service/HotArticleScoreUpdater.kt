package reehi.board.hotarticle.service

import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.hotarticle.repository.ArticleCreatedTimeRepository
import reehi.board.hotarticle.repository.HotArticleListRepository
import reehi.board.hotarticle.service.eventhandler.EventHandler
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class HotArticleScoreUpdater (
    val hotArticleListRepository: HotArticleListRepository,
    val hotArticleScoreCalculator: HotArticleScoreCalculator,
    val articleCreatedTimeRepository: ArticleCreatedTimeRepository

){
    companion object {
        const val HOT_ARTICLE_COUNT: Long = 10
        val HOT_ARTICLE_TTL: Duration = Duration.ofDays(10)

    }
    fun update(event: Event<EventPayload>, eventHandler: EventHandler<EventPayload>?) {
        val articleId: Long = eventHandler?.findArticleId(event) ?: return
        val createdTime = articleCreatedTimeRepository.read(articleId)

        if (!isArticleCreatedToday(createdTime)) {
            return
        }

        eventHandler.handle(event)

        val score: Long = hotArticleScoreCalculator.calculate(articleId)
        hotArticleListRepository.add(
            articleId,
            createdTime!!,
            score,
            HOT_ARTICLE_COUNT,
            HOT_ARTICLE_TTL
        )
    }

    private fun isArticleCreatedToday(createdTime: LocalDateTime?): Boolean {
        return createdTime != null && createdTime.toLocalDate() == LocalDate.now()
    }
}