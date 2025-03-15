package reehi.board.common.outboxmessagerelay

import kuke.board.common.snowflake.Snowflake
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload
import reehi.board.common.event.EventType


@Component
class OutboxEventPublisher (
    val applicationEventPublisher: ApplicationEventPublisher
){
    val outboxIdSnowflake: Snowflake = Snowflake()
    val eventIdSnowflake: Snowflake = Snowflake()

    fun publish(type: EventType, payload: EventPayload, shardKey: Long) {
        val outbox = Event.of(
            eventIdSnowflake.nextId(), type, payload
        ).toJson()?.let {
            Outbox.create(
                outboxIdSnowflake.nextId(),
                type,
                it,
                shardKey % MessageRelayConstants.SHARD_COUNT
            )
        }
        applicationEventPublisher.publishEvent(OutboxEvent.of(outbox!!))
    }
}