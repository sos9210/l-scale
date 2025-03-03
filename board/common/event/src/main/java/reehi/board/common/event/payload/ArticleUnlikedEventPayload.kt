package reehi.board.common.event.payload

import reehi.board.common.event.EventPayload
import java.time.LocalDateTime



class ArticleUnlikedEventPayload(
    private val articleLikeId: Long,
    private val articleId: Long,
    private val userId: Long,
    private val createdAt: LocalDateTime,
    private val articleLikeCount: Long,
): EventPayload {
}