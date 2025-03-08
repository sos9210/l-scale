package reehi.board.common.event

import org.junit.jupiter.api.assertInstanceOf
import reehi.board.common.event.payload.ArticleCreatedEventPayload
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue


class EventTest {
    @Test
    fun serde() {
        // given
        val payload: ArticleCreatedEventPayload = ArticleCreatedEventPayload(
            articleId = 1L,
            title = "title",
            content = "content",
            boardId = 1L,
            writerId = 1L,
            createdAt = LocalDateTime.now(),
            modifiedAt = LocalDateTime.now(),
            boardArticleCount = 23L,
        )
        val event = Event.of(
            1234L,
            EventType.ARTICLE_CREATED,
            payload
        )

        val json = event.toJson()
        println("json = $json")

        // when
        val result = Event.fromJson<Any>(json!!)!!

        // then
        assertEquals(result.eventId,event.eventId)
        assertEquals(result.type,event.type)
        assertTrue(result.payload!!::class == payload::class)

        val resultPayload = result.payload as ArticleCreatedEventPayload
        assertEquals(resultPayload.articleId,payload.articleId)
        assertEquals(resultPayload.title,payload.title)
        assertEquals(resultPayload.createdAt,payload.createdAt)
    }
}