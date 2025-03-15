package reehi.board.hotarticle.service

import org.springframework.stereotype.Service
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.common.event.EventType
import reehi.board.hotarticle.client.ArticleClient
import reehi.board.hotarticle.repository.HotArticleListRepository
import reehi.board.hotarticle.service.eventhandler.EventHandler
import reehi.board.hotarticle.service.response.HotArticleResponse
import java.util.*

@Service
class HotArticleService (
    private val articleClient: ArticleClient,
    private val eventHandlers: MutableList<EventHandler<EventPayload>>,
    private val hotArticleScoreUpdater: HotArticleScoreUpdater,
    private val hotArticleListRepository: HotArticleListRepository
){

    fun handleEvent(event: Event<EventPayload>) {
        val eventHandler: EventHandler<EventPayload>? = findEventHandler(event)

        if (isArticleCreatedOrDeleted(event)) {
            eventHandler?.handle(event)
        } else {
            hotArticleScoreUpdater.update(event, eventHandler)
        }
    }

    private fun findEventHandler(event: Event<EventPayload>): EventHandler<EventPayload>? {
        return eventHandlers.stream()
            .filter { eventHandler: EventHandler<EventPayload> -> eventHandler.supports(event) }
            .findAny()
            .orElse(null)
    }

    private fun isArticleCreatedOrDeleted(event: Event<EventPayload>): Boolean {
        return EventType.ARTICLE_CREATED === event.type || EventType.ARTICLE_DELETED === event.type
    }

    fun readAll(dateStr: String): MutableList<HotArticleResponse> {
        return hotArticleListRepository.readAll(dateStr).stream()
            .map<ArticleClient.Companion.ArticleResponse> { articleId: Long -> articleClient.read(articleId) }
            .filter { obj: ArticleClient.Companion.ArticleResponse -> Objects.nonNull(obj) }
            .map(HotArticleResponse::from)
            .toList()
    }
}