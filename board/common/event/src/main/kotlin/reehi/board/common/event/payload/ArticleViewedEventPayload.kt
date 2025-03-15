package reehi.board.common.event.payload

import reehi.board.common.event.EventPayload

class ArticleViewedEventPayload(
    val articleId: Long,
    val articleViewCount: Long,
) : EventPayload {
}