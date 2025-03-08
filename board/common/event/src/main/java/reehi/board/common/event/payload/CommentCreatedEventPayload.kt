package reehi.board.common.event.payload

import reehi.board.common.event.EventPayload
import java.time.LocalDateTime



class CommentCreatedEventPayload(
     val commentId: Long,
     val content: String,
     val path: String,
     val articleId: Long,
     val writerId: Long,
     val deleted: Boolean,
     val createdAt: LocalDateTime,
     val articleCommentCount: Long,
): EventPayload {
}