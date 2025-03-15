package reehi.board.common.event

import reehi.board.common.dataserializer.DataSerializer

class Event<T : EventPayload?> (
    val eventId: Long,
    val type: EventType?,
    val payload: T
){

    fun toJson(): String? {
        return DataSerializer.serialize(this)
    }


    companion object {

        private class EventRaw (
            val eventId: Long,
            val type: String,
            val payload: Any
        )

        fun of(eventId: Long, type: EventType, payload: EventPayload): Event<EventPayload> = Event(
            eventId = eventId,
            type = type,
            payload = payload

        )

        fun <T> fromJson(json: String): Event<EventPayload>? {
            val eventRaw: EventRaw = DataSerializer.deserialize(json, EventRaw::class.java) ?: return null
            val event: Event<EventPayload> =  Event(
                eventId = eventRaw.eventId,
                type = EventType.from(eventRaw.type),
                payload = DataSerializer.deserialize(eventRaw.payload, EventType.from(eventRaw.type)!!.payloadClass)
            )
            return event

        }
    }
}