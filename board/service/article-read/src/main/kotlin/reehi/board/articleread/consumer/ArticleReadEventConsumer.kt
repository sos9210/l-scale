package reehi.board.articleread.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import reehi.board.articleread.service.ArticleReadService
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.common.event.EventType


@Component
class ArticleReadEventConsumer (
    val articleReadService: ArticleReadService
){

    @KafkaListener(
        topics = [
            EventType.Companion.Topic.REEHI_BOARD_ARTICLE,
            EventType.Companion.Topic.REEHI_BOARD_COMMENT,
            EventType.Companion.Topic.REEHI_BOARD_LIKE
        ]
    )
    fun listen(message: String, ack: Acknowledgment) {
        println("[ArticleReadEventConsumer.listen] message=$message")

        val event: Event<EventPayload>? = Event.fromJson<EventPayload>(message)
        event?.let {
            articleReadService.handleEvent(event)
        }
        ack.acknowledge()
    }

}