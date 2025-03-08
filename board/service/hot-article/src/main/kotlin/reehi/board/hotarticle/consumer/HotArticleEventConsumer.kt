package reehi.board.hotarticle.consumer

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.common.event.EventType
import reehi.board.hotarticle.service.HotArticleService


@Component
class HotArticleEventConsumer (
    val hotArticleService: HotArticleService

){

    @KafkaListener(
        topics = [
            EventType.Companion.Topic.REEHI_BOARD_ARTICLE,
            EventType.Companion.Topic.REEHI_BOARD_COMMENT,
            EventType.Companion.Topic.REEHI_BOARD_LIKE,
            EventType.Companion.Topic.REEHI_BOARD_VIEW
        ]
    )
    fun listen(message: String, ack: Acknowledgment) {
        println("[HotArticleEventConsumer.listen] received message=$message")
        val event = Event.fromJson<EventPayload>(message)
        if (event != null) {
            hotArticleService.handleEvent(event)
        }
        ack.acknowledge()
    }

}