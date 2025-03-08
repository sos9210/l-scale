package reehi.board.hotarticle.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.hotarticle.repository.ArticleCreatedTimeRepository
import reehi.board.hotarticle.repository.HotArticleListRepository
import reehi.board.hotarticle.service.eventhandler.EventHandler
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class HotArticleScoreUpdaterTest {
    @InjectMockKs
    lateinit var hotArticleScoreUpdater: HotArticleScoreUpdater

    @MockK
    lateinit var hotArticleListRepository: HotArticleListRepository

    @MockK
    lateinit var hotArticleScoreCalculator: HotArticleScoreCalculator

    @MockK
    lateinit var articleCreatedTimeRepository: ArticleCreatedTimeRepository

    @Test
    fun updateIfArticleNotCreatedTodayTest() {
        // given
        val articleId = 1L
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>()

        every { eventHandler.findArticleId(event) } returns articleId

        val createdTime = LocalDateTime.now().minusDays(1)
        every { articleCreatedTimeRepository.read(articleId) } returns createdTime

        // when
        hotArticleScoreUpdater.update(event, eventHandler)

        // then
        verify(exactly = 0) { eventHandler.handle(event) }
        verify(exactly = 0) {
            hotArticleListRepository.add(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun updateTest() {
        // given
        val articleId = 1L
        val event = mockk<Event<EventPayload>>()
        val eventHandler = mockk<EventHandler<EventPayload>>()

        every { eventHandler.findArticleId(event) } returns articleId

        val createdTime = LocalDateTime.now()
        every { articleCreatedTimeRepository.read(articleId) } returns createdTime
        every { eventHandler.handle(event) } returns Unit
        every { hotArticleScoreCalculator.calculate(articleId) }

        // when
        hotArticleScoreUpdater.update(event, eventHandler)

        // then
        verify { eventHandler.handle(event) }
        verify {
            hotArticleListRepository.add(
                any(),
                any(),
                any(),
                any(),
                any()
            )
        }
    }
}
