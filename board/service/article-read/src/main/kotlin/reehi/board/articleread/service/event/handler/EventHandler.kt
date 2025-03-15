package reehi.board.articleread.service.event.handler

import reehi.board.common.event.Event
import reehi.board.common.event.EventPayload


interface EventHandler<T : EventPayload> {
    fun handle(event: Event<T>)
    fun supports(event: Event<T>): Boolean
}