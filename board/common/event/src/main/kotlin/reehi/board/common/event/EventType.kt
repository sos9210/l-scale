package reehi.board.common.event

import reehi.board.common.event.payload.*

enum class EventType (
    val payloadClass: Class<out EventPayload>,
    val topic: String
){
    ARTICLE_CREATED(ArticleCreatedEventPayload::class.java, Topic.REEHI_BOARD_ARTICLE),
    ARTICLE_UPDATED(ArticleUpdatedEventPayload::class.java, Topic.REEHI_BOARD_ARTICLE),
    ARTICLE_DELETED(ArticleDeletedEventPayload::class.java, Topic.REEHI_BOARD_ARTICLE),
    COMMENT_CREATED(CommentCreatedEventPayload::class.java, Topic.REEHI_BOARD_COMMENT),
    COMMENT_DELETED(CommentDeletedEventPayload::class.java, Topic.REEHI_BOARD_COMMENT),
    ARTICLE_LIKED(ArticleLikedEventPayload::class.java, Topic.REEHI_BOARD_LIKE),
    ARTICLE_UNLIKED(ArticleUnlikedEventPayload::class.java, Topic.REEHI_BOARD_LIKE),
    ARTICLE_VIEWED(ArticleViewedEventPayload::class.java, Topic.REEHI_BOARD_VIEW);


    companion object {
        object Topic {
            const val REEHI_BOARD_ARTICLE: String = "reehi-board-article"
            const val REEHI_BOARD_COMMENT: String = "reehi-board-comment"
            const val REEHI_BOARD_LIKE: String = "reehi-board-like"
            const val REEHI_BOARD_VIEW: String = "reehi-board-view"
        }

        fun from(type: String): EventType? {
            try {
                return valueOf(type)
            } catch (e: Exception) {
                println("[EventType.from] type=$type")
                return null
            }
        }
    }
}