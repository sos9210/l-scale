package reehi.board.hotarticle.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.common.event.EventType
import reehi.board.hotarticle.service.eventhandler.EventHandler

@ExtendWith(MockKExtension::class)
class HotArticleServiceTest {
    @InjectMockKs
    lateinit var hotArticleService: HotArticleService

    @MockK
    lateinit var eventHandlers: List<EventHandler<EventPayload>>

    @MockK
    lateinit var hotArticleScoreUpdater: HotArticleScoreUpdater

    @Test
    fun handleEventIfEventHandlerNotFoundTest() {
        // given
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>()
        every { eventHandler.supports(event) } returns false
        every { eventHandlers.stream() } returns listOf(eventHandler).stream()

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(exactly = 0) { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleScoreUpdater.update(event, eventHandler) }
    }

    @Test
    fun handleEventIfArticleCreatedEventTest() {
        // given
        val event = mockk<Event<EventPayload>>()
        every { event.type } returns EventType.ARTICLE_CREATED

        val eventHandler = mockk<EventHandler<EventPayload>>()
        every { eventHandler.supports(event) } returns true
        every { eventHandlers.stream() } returns listOf(eventHandler).stream()

        // when
        hotArticleService.handleEvent(event)

        // then
        verify { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleScoreUpdater.update(event, eventHandler) }
    }

    @Test
    fun handleEventIfArticleDeletedEventTest() {
        // given
        val event = mockk<Event<EventPayload>>()
        every { event.type } returns EventType.ARTICLE_DELETED

        val eventHandler = mockk<EventHandler<EventPayload>>()
        every { eventHandler.supports(event) } returns true
        every { eventHandlers.stream() } returns listOf(eventHandler).stream()

        // when
        hotArticleService.handleEvent(event)

        // then
        verify { eventHandler.handle(event) }
        verify(exactly = 0) { hotArticleScoreUpdater.update(event, eventHandler) }
    }

    @Test
    fun handleEventIfScoreUpdatableEventTest() {
        // given
        val event = mockk<Event<EventPayload>>()
        every { event.type } returns mockk()

        val eventHandler = mockk<EventHandler<EventPayload>>()
        every { eventHandler.supports(event) } returns true
        every { eventHandlers.stream() } returns listOf(eventHandler).stream()

        // when
        hotArticleService.handleEvent(event)

        // then
        verify(exactly = 0) { eventHandler.handle(event) }
        verify { hotArticleScoreUpdater.update(event, eventHandler) }
    }
}
