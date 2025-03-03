package reehi.board.common.event.payload

import reehi.board.common.event.EventPayload
import java.time.LocalDateTime



class ArticleCreatedEventPayload(
    val articleId: Long,
    val title: String,
    val content: String,
    val boardId: Long,
    val writerId: Long,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val boardArticleCount: Long
) : EventPayload
{
}