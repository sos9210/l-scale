package reehi.board.common.event.payload

import reehi.board.common.event.EventPayload

class ArticleViewedEventPayload(
    private val articleId: Long,
    private val articleViewCount: Long,
) : EventPayload {
}